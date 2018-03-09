package com.wuda.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * jdbc辅助类.
 *
 * @author wuda
 */
public class Jdbcs {

    private static Logger logger = LoggerFactory.getLogger(Jdbcs.class);

    /**
     * 查询数据库,流式处理每一条记录.
     * 只有当<code>Stream</code>执行了终端方法时,才会链接数据库,然后执行sql语句.
     * 由于链接了数据库,并且执行了sql语句,因此必须正确的调用{@link Stream#close()}方法,以便关闭数据库资源.
     *
     * @param dataSource
     *         数据源
     * @param sql
     *         ＳＱＬ语句,只能是查询语句
     * @param rowMapper
     *         将数据库中的记录转换成java bean,注意千万不要调用{@link ResultSet#next()}方法.
     * @param <R>
     *         转换后的java bean类型
     * @return Stream
     */
    public static <R> Stream<R> query(DataSource dataSource, String sql, Function<ResultSet, R> rowMapper) {
        Objects.requireNonNull(dataSource, "DataSource不能为空!");
        Objects.requireNonNull(sql, "sql语句不能为空!");
        Objects.requireNonNull(rowMapper, "rowMapper不能为空!");
        if (!isSelectStatement(sql)) {
            throw new RuntimeException("sql只能是查询语句!sql=" + sql);
        }
        ResultSetSpliterator<R> spliterator = new ResultSetSpliterator<>(dataSource, sql, rowMapper);
        return StreamSupport.stream(spliterator, true)
                .onClose(spliterator.closeDbSourceTask());
    }

    /**
     * 是否查询语句.
     *
     * @param sql
     *         sql
     * @return true-是查询语句
     */
    public static boolean isSelectStatement(String sql) {
        sql = sql.trim();
        if (sql.length() <= 6) { // 光【select】就有６个字符了,如果是正常的select语句,长度不可能小于６
            return false;
        }
        String prefix = sql.substring(0, 6);
        return prefix.equalsIgnoreCase("select");
    }

    /**
     * 关闭资源.
     *
     * @param connection
     *         如果不为null,则执行close
     * @param statement
     *         如果不为null,则执行close
     * @param resultSet
     *         如果不为null,则执行close
     */
    public static void silenceClose(Connection connection, Statement statement, ResultSet resultSet) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // silence
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                // silence
            }
        }
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                // silence
            }
        }
        logger.error("close db source!");
    }

    /**
     * 首先将数据库数据转换成java bean,然后再将java bean放入Spliterator中.
     *
     * @param <T>
     *         Spliterator中包含的元素,也就是转换得到的java bean的类型.
     * @author wuda
     */
    private static class ResultSetSpliterator<T> implements Spliterator<T> {

        /**
         * 当前spliterator使用的数据源.
         */
        private final DataSource dataSource;
        /**
         * sql语句.
         */
        private String sql;
        /**
         * {@link ResultSet}转java bean的转换器.
         */
        private Function<ResultSet, T> rowMapper;
        /**
         * 分隔单位,每达到当前指定的分隔大小时,进行一次分隔.
         */
        private int splitSize = 128;

        /**
         * 数据库连接对象.
         */
        private Connection connection;
        /**
         * statement.
         */
        private Statement statement;
        /**
         * result set.
         */
        private ResultSet resultSet;

        /**
         * 生成spliterator.使用{@link ResultSet}作为数据来源,并且使用<code>rowMapper</code>可以将数据转换成java
         * bean.
         *
         * @param dataSource
         *         数据源
         * @param sql
         *         sql语句
         * @param rowMapper
         *         将数据库数据映射为实体
         */
        private ResultSetSpliterator(final DataSource dataSource, String sql, Function<ResultSet, T> rowMapper) {
            this.dataSource = dataSource;
            this.sql = sql;
            this.rowMapper = rowMapper;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            // 把握一切机会关闭数据库资源
            Jdbcs.silenceClose(connection, statement, resultSet);
            // 全部都分出去了,这里不会有待处理元素
            return false;
        }

        @Override
        public Spliterator<T> trySplit() {
            if (resultSet == null && acquireChanceToExecuteQuery()) {
                try {
                    connection = dataSource.getConnection();
                    connection.setReadOnly(true);
                    /*
                      stream result set for mysql.
                      <a href="https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-implementation-notes.html">JDBC API Implementation Notes</a>
                     */
                    statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                    statement.setFetchSize(Integer.MIN_VALUE);
                    resultSet = statement.executeQuery(sql);
                    logger.error("resultSet={}", resultSet);
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                    Jdbcs.silenceClose(connection, statement, resultSet);
                    return null;
                }
            }
            try {
                if (!resultSet.next()) {
                    Jdbcs.silenceClose(connection, statement, resultSet);
                    return null;
                }
            } catch (Exception e) {
                Jdbcs.silenceClose(connection, statement, resultSet);
                logger.warn("resultSet.next()方法调用时异常,{}", e.getMessage(), e);
                return null;
            }
            int count = 0;
            Object[] array = new Object[splitSize];
            try {
                do {
                    array[count] = rowMapper.apply(resultSet);
                    count++;
                } while (count < splitSize && resultSet.next());
            } catch (SQLException e) {
                logger.warn("resultSet.next()方法调用时异常,{}", e.getMessage(), e);
                if (count == 0) {
                    Jdbcs.silenceClose(connection, statement, resultSet);
                    return null;
                }
            }
            return Spliterators.spliterator(array, count);
        }

        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return CONCURRENT;
        }

        /**
         * sql语句是否已经执行.
         */
        private AtomicBoolean queryExecuted = new AtomicBoolean(false);

        /**
         * 获取执行sql语句的机会.
         *
         * @return true-可已执行,false-不能执行
         */
        private boolean acquireChanceToExecuteQuery() {
            while (!queryExecuted.get()) {
                if (queryExecuted.compareAndSet(false, true)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 关闭数据库连接的任务.
         *
         * @return task
         */
        private Runnable closeDbSourceTask() {
            return () -> Jdbcs.silenceClose(connection, statement, resultSet);
        }
    }

}
