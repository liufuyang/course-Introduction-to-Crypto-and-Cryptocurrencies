import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TxHandler {

    private UTXOPool utxoPool;

    public TxHandler(UTXOPool utxoPool) {
        this.utxoPool = new UTXOPool(utxoPool);
    }

    public boolean isValidTx(Transaction tx) {
                boolean isValid = tx.getInputs().stream()
                        .map(input -> new UTXO(input.prevTxHash, input.outputIndex))
                        .allMatch(utxo -> utxoPool.contains(utxo));

                isValid = isValid && IntStream.range(0, tx.getInputs().size())
                        .allMatch(i -> {
                            Transaction.Output previousOutput = utxoPool.getTxOutput(new UTXO(tx.getInput(i).prevTxHash, tx.getInput(i).outputIndex));
                            byte[] m = tx.getRawDataToSign(i);
                            byte[] sig = tx.getInput(i).signature;
                            boolean sigValid = previousOutput.address.verifySignature(m, sig); // use this for passing local check
                            //boolean sigValid = Crypto.verifySignature(previousOutput.address, m, sig); // use this for homework submission
                            return sigValid;
                        });

                isValid = isValid && (tx.getInputs().stream().map(input -> new UTXO(input.prevTxHash, input.outputIndex)).distinct().count()
                        == tx.getInputs().stream().count()
                );

                isValid = isValid && tx.getOutputs().stream().allMatch(output -> output.value >= 0.0);

                isValid = isValid && (tx.getInputs().stream().mapToDouble(input -> utxoPool.getTxOutput(new UTXO(input.prevTxHash, input.outputIndex)).value).sum() >= tx.getOutputs().stream().mapToDouble(output -> output.value).sum());

        return isValid;
    }

    public Transaction[] handleTxs(Transaction[] possibleTxs) {

                return Stream.of(possibleTxs).filter(transaction -> {
                    boolean isValid = isValidTx(transaction);

                    if (isValid) {
                        IntStream.range(0, transaction.getInputs().size())
                                .forEach(i -> {
                                    utxoPool.removeUTXO(new UTXO(transaction.getInput(i).prevTxHash, transaction.getInput(i).outputIndex));
                                    utxoPool.addUTXO(new UTXO(transaction.getHash(), i), transaction.getOutput(i));
                                });

                        return true;
                    } else {
                        return false;
                    }
                }).toArray(Transaction[]::new);

    }
}
