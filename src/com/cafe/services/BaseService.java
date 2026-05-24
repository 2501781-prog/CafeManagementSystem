package com.cafe.services;

import java.sql.SQLException;
import java.util.List;

public abstract class BaseService<T> {
    public abstract boolean add(T entity) throws SQLException;

    public abstract boolean update(T entity) throws SQLException;

    public abstract boolean delete(int id) throws SQLException;

    public abstract List<T> search(String keyword) throws SQLException;

    public abstract List<T> getAll() throws SQLException;
}

