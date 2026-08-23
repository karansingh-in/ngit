package ngit.commands;

import ngit.repository.Repository;
import ngit.utils.Checkout;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DataFormatException;

public class CheckoutCommand {
    private Repository repo;

    public CheckoutCommand(Repository repo){
        this.repo = repo;
    }

    public void Checkout(String branchName) throws DataFormatException, IOException, NoSuchAlgorithmException {
        Checkout ch = new Checkout(repo);
        ch.switchBranch(branchName);
    }
}
