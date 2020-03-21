package org.dpr.mykeys.app.keystore.repository;

public class EntityAlreadyExistsException extends RepositoryException {
    public EntityAlreadyExistsException(String s) {
        super(s);
    }

    public EntityAlreadyExistsException(String s, Exception e) {
        super(s, e);
    }
}
