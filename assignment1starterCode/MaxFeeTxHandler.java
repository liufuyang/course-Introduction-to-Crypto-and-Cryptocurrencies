
public class MaxFeeTxHandler {
    public UTXOPool utxoPool;

    public MaxFeeTxHandler(UTXOPool utxoPool) {
        this.utxoPool = new UTXOPool(utxoPool);
    }

    public boolean isValidTx(Transaction tx) {

        return true;
    }

    public Transaction[] handleTxs(Transaction[] possibleTxs) {

        return possibleTxs;
    }

    public UTXOPool getUtxoPool() {
        return utxoPool;
    }

    public void setUtxoPool(UTXOPool utxoPool) {
        this.utxoPool = utxoPool;
    }
}
