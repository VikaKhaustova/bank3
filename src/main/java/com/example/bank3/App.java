package com.example.bank3;


import javax.persistence.*;
import java.util.*;

public class App {
    static EntityManagerFactory emf;
    static EntityManager em;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            emf = Persistence.createEntityManagerFactory("bank");
            em = emf.createEntityManager();
            try {
                while (true) {
                    System.out.println("1: add client");
                    System.out.println("2: view clients");
                    System.out.println("3: add transaction");
                    System.out.println("4: view accounts");
                    System.out.println("5: replenish account");
                    System.out.println("6: convert сurrency");
                    System.out.println("7: get total balance in UAH");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addClient(sc);
                            break;
                        case "2":
                            viewClients();
                            break;
                        case "3":
                            addTransaction(sc);
                            break;
                        case "4":
                            viewAccounts();
                            break;
                        case "5":
                            replenishAccount(sc);
                            break;
                        case "6":
                            convertCurrency(sc);
                            break;
                        case "7":
                            getTotalBalanceInUAH(sc);
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                em.close();
                emf.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    private static void addClient(Scanner sc) {
        System.out.print("Enter firstName: ");
        String firstName = sc.nextLine();
        System.out.print("Enter lastName: ");
        String lastName = sc.nextLine();
        System.out.print("Enter email: ");
        String email = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        em.getTransaction().begin();
        try {
            User user = new User(firstName, lastName, email, password);
            em.persist(user);

            System.out.print("Enter amount UAH: ");
            double amountUAH = Double.parseDouble(sc.nextLine());
            System.out.print("Enter amount USD: ");
            double amountUSD = Double.parseDouble(sc.nextLine());
            System.out.print("Enter amount EUR: ");
            double amountEUR = Double.parseDouble(sc.nextLine());

            Account account = new Account(user, amountUAH, amountUSD, amountEUR);
            em.persist(account);

            user.setAccount(account);

            em.getTransaction().commit();
            System.out.println("Client and account added successfully.");
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void viewClients() {
        Query query = em.createQuery("SELECT c FROM User c", User.class);
        List<User> list = (List<User>) query.getResultList();

        for (User c : list)
            System.out.println(c);
    }

    private static void viewAccounts() {
        Query query = em.createQuery("SELECT c FROM Account c", Account.class);
        List<Account> list = (List<Account>) query.getResultList();

        for (Account c : list)
            System.out.println(c);
    }

    private static void addTransaction(Scanner sc) {
        System.out.print("Enter sender account ID: ");
        long senderAccountId = Long.parseLong(sc.nextLine());

        System.out.print("Enter receiver account ID: ");
        long receiverAccountId = Long.parseLong(sc.nextLine());

        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(sc.nextLine());

        Account senderAccount = em.find(Account.class, senderAccountId);
        Account receiverAccount = em.find(Account.class, receiverAccountId);

        if (senderAccount == null || receiverAccount == null) {
            System.out.println("Account not found!");
            return;
        }

        System.out.println("Choose currency for the transaction (UAH, USD, EUR): ");
        String currencyStr = sc.nextLine();
        CurrencyType currency = null;

        try {
            currency = CurrencyType.valueOf(currencyStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid currency selection.");
            return;
        }

        double senderBalance = 0.0;
        double receiverBalance = 0.0;

        switch (currency) {
            case UAH:
                senderBalance = senderAccount.getUAH();
                receiverBalance = receiverAccount.getUAH();
                break;
            case USD:
                senderBalance = senderAccount.getUSD();
                receiverBalance = receiverAccount.getUSD();
                break;
            case EUR:
                senderBalance = senderAccount.getEUR();
                receiverBalance = receiverAccount.getEUR();
                break;
            default:
                System.out.println("Invalid currency selection.");
                return;
        }

        if (senderBalance < amount) {
            System.out.println("Insufficient funds in the sender's account!");
            return;
        }

        em.getTransaction().begin();
        try {
            switch (currency) {
                case UAH:
                    senderAccount.setUAH(senderAccount.getUAH() - amount);
                    receiverAccount.setUAH(receiverAccount.getUAH() + amount);
                    break;
                case USD:
                    senderAccount.setUSD(senderAccount.getUSD() - amount);
                    receiverAccount.setUSD(receiverAccount.getUSD() + amount);
                    break;
                case EUR:
                    senderAccount.setEUR(senderAccount.getEUR() - amount);
                    receiverAccount.setEUR(receiverAccount.getEUR() + amount);
                    break;
                default:
                    break;
            }


            java.util.Date utilDate = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

            Transaction transaction = new Transaction(senderAccount.getUser(), receiverAccount.getUser(), amount, currency.name(), sqlDate);

            em.persist(transaction);

            em.getTransaction().commit();

            System.out.println("Transaction completed successfully.");
        } catch (Exception ex) {
            em.getTransaction().rollback();
            System.out.println("Transaction failed.");
        }
    }



    private static void replenishAccount(Scanner sc) {
        System.out.print("Enter account ID to replenish: ");
        long accountId = Long.parseLong(sc.nextLine());

        Account account = em.find(Account.class, accountId);

        if (account == null) {
            System.out.println("Account not found!");
            return;
        }

        System.out.println("Choose currency for replenishment (UAH, USD, EUR): ");
        String currencyStr = sc.nextLine();
        CurrencyType currency = null;

        try {
            currency = CurrencyType.valueOf(currencyStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid currency selection.");
            return;
        }

        System.out.print("Enter amount to replenish: ");
        double amountToReplenish = Double.parseDouble(sc.nextLine());

        em.getTransaction().begin();
        try {
            switch (currency) {
                case UAH:
                    account.setUAH(account.getUAH() + amountToReplenish);
                    break;
                case USD:
                    account.setUSD(account.getUSD() + amountToReplenish);
                    break;
                case EUR:
                    account.setEUR(account.getEUR() + amountToReplenish);
                    break;
                default:
                    System.out.println("Invalid currency selection.");
                    return;
            }

            em.getTransaction().commit();
            System.out.println("Account replenished successfully.");
        } catch (Exception ex) {
            em.getTransaction().rollback();
            System.out.println("Failed to replenish account.");
        }
    }

    private static void convertCurrency(Scanner sc) {
        System.out.print("Enter user ID: ");
        long userId = Long.parseLong(sc.nextLine());

        try {
            em.getTransaction().begin();

            User user = em.find(User.class, userId);

            if (user == null) {
                System.out.println("User not found!");
                return;
            }

            Account account = user.getAccount();

            if (account == null) {
                System.out.println("User's account not found!");
                return;
            }

            System.out.print("Enter source currency (UAH, USD, EUR): ");
            String sourceCurrencyStr = sc.nextLine();
            CurrencyType sourceCurrency = null;

            try {
                sourceCurrency = CurrencyType.valueOf(sourceCurrencyStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid source currency.");
                return;
            }

            System.out.print("Enter target currency (UAH, USD, EUR): ");
            String targetCurrencyStr = sc.nextLine();
            CurrencyType targetCurrency = null;

            try {
                targetCurrency = CurrencyType.valueOf(targetCurrencyStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid target currency.");
                return;
            }

            System.out.print("Enter conversion rate (1 " + sourceCurrency + " = ? " + targetCurrency + "): ");
            double conversionRate = Double.parseDouble(sc.nextLine());

            System.out.print("Enter amount to convert: ");
            double amountToConvert = Double.parseDouble(sc.nextLine());

            double convertedAmount = amountToConvert * conversionRate;

            switch (sourceCurrency) {
                case UAH:
                    account.setUAH(account.getUAH() - amountToConvert);
                    break;
                case USD:
                    account.setUSD(account.getUSD() - amountToConvert);
                    break;
                case EUR:
                    account.setEUR(account.getEUR() - amountToConvert);
                    break;
                default:

                    break;
            }

            switch (targetCurrency) {
                case UAH:
                    account.setUAH(account.getUAH() + convertedAmount);
                    break;
                case USD:
                    account.setUSD(account.getUSD() + convertedAmount);
                    break;
                case EUR:
                    account.setEUR(account.getEUR() + convertedAmount);
                    break;
                default:
                    break;
            }

            em.getTransaction().commit();
            System.out.println("Currency conversion completed successfully.");
        } catch (Exception ex) {

            em.getTransaction().rollback();
            System.out.println("Currency conversion failed.");
        }
    }

    private static void getTotalBalanceInUAH(Scanner sc) {
        System.out.print("Enter user ID: ");
        long userId = Long.parseLong(sc.nextLine());


        User user = em.find(User.class, userId);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        double balanceInUAH = 0.0;


        Account account = user.getAccount();
        balanceInUAH = account.getUAH() + account.getUSD() * 37.03 + account.getEUR() * 40.55;

        System.out.println("Total balance in UAH for user ID " + userId + ": " + balanceInUAH);
    }

}
