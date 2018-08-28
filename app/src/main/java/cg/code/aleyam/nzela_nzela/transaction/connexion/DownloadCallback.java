package cg.code.aleyam.nzela_nzela.transaction.connexion;

import java.util.ArrayList;

public interface DownloadCallback{

    void downloaded(ArrayList<? extends TransactionPost> data);
    void failed(String message, boolean tryOption);
    void added(TransactionPost new_data);


}