package com.example.bank3;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

@Controller
public class TestSpringController {
    static EntityManagerFactory emf;
    static EntityManager em;

    @GetMapping("/test")
    public String showMessage() {
        return "index.html";
    }

    @GetMapping("/transactions")
    public String viewAllTransactions(Model model) {
        emf = Persistence.createEntityManagerFactory("bank");
        em = emf.createEntityManager();
        List<Transaction> transactions = em.createQuery("SELECT t FROM Transaction t", Transaction.class).getResultList();
        em.close();
        model.addAttribute("transactions", transactions);
        return "transactions.html";
    }

    @GetMapping("/total-balance")
    public String viewTotalBalanceAllClients(Model model) {
        emf = Persistence.createEntityManagerFactory("bank");
        em = emf.createEntityManager();

        double totalBalance = 0.0;

        List<User> users = em.createQuery("SELECT a FROM User a", User.class).getResultList();

        for (User user : users) {
            totalBalance += user.getAccount().getUAH();
            totalBalance += user.getAccount().getEUR() * 37.03;
            totalBalance += user.getAccount().getUSD() * 40.55;
        }
        em.close();

        model.addAttribute("totalBalance", totalBalance);
        return "total-balance.html";
    }
}