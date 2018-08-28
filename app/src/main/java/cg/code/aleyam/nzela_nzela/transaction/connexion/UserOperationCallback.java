package cg.code.aleyam.nzela_nzela.transaction.connexion;

public interface UserOperationCallback {
    void succes(UserObject userObject);
    void failed();
    void operationResult(String key, Object object);

}