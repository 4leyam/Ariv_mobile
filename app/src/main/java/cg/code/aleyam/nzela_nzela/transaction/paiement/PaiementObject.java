package cg.code.aleyam.nzela_nzela.transaction.paiement;

public class PaiementObject {

    public String amount  = "";
    public String nonce = "";

    public PaiementObject() {

    }

    public PaiementObject(String amount , String  nonce) {
        this.amount = amount;
        this.nonce = nonce;
    }

    public String getAmount() {
        return amount;
    }

    public String getNonce() {
        return nonce;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
