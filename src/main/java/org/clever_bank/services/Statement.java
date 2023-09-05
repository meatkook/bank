package org.clever_bank.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import org.clever_bank.entities.Account;
import org.clever_bank.entities.Transaction;
import org.clever_bank.repository.TransactionRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Statement {
    public static void createTransactionCheck(Transaction transaction) {
        String transactionId = String.valueOf(transaction.getId());
        String date = getStringDate(transaction.getDate(), 1);
        String time = getStringTime(transaction.getDate(), 1);
        String transactionType = transaction.getType().getName();
        String bankSenderName = transaction.getAccountSender().getBank().getName();
        String bankRecipientName = transaction.getAccountRecipient().getBank().getName();
        String accountSenderNumber = transaction.getAccountSender().getAccountNumber();
        String accountRecipientNumber = transaction.getAccountRecipient().getAccountNumber();
        String money = transaction.getMoney().toString();
        String currency = transaction.getAccountSender().getCurrency();
        String bankCheck = "";
        try {
            String underFooter = "_".repeat(56) + "\n";
            String header = "| " + " ".repeat(19) + "Банковский чек" + " ".repeat(19) + " |\n";
            String check = "| Чек:" + " ".repeat(48 - transactionId.length()) + transactionId +" |\n";
            String dataTime = "| " + date + " ".repeat(34) + time +" |\n";
            String type = "| Тип транзакции:" + " ".repeat(37 - transactionType.length()) + transactionType + " |\n";
            String firstBank = "| Банк отправителя: " + " ".repeat(34 - bankSenderName.length()) + bankSenderName + " |\n";
            String secondBank = "| Банк получателя: " + " ".repeat(35 - bankRecipientName.length()) + bankRecipientName + " |\n";
            String firstAccount = "| Счёт отправителя: " + " ".repeat(34 - accountSenderNumber.length()) + accountSenderNumber + " |\n";
            String secondAccount = "| Счёт получателя: " + " ".repeat(35 - accountRecipientNumber.length()) + accountRecipientNumber + " |\n";
            String amount = "| Сумма: " + " ".repeat(41 - money.length()) + money + " " + currency + " |\n";
            String footer = "|" + "_".repeat(54) + "|\n";

            bankCheck = underFooter +
                    header +
                    check +
                    dataTime +
                    type +
                    firstBank +
                    secondBank +
                    firstAccount +
                    secondAccount +
                    amount +
                    footer;
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        if (createNewDirectory("check") == -1){
            System.out.println("Error when creating a directory");
        }
        stringToPdfFile (bankCheck, "check/Check");
        System.out.println(bankCheck);
        System.out.println("Чек сохранён");
    }

    public static void createReport(Account account, Instant dateStart, Instant dateEnd){
        String currency = account.getCurrency();

        // transactions list
        StringBuilder transactionLines = new StringBuilder();
        List<Transaction> transactions = TransactionRepository.readTransactionsPeriod(account.getId(), dateStart, dateEnd);
        for (Transaction transaction : transactions) {
            String transactionDate = getStringDate(transaction.getDate(), 3);
            String note;
            if (transaction.getType().getId() == 3){
                note = getNoteIfTransactionEqualsTree(transaction, account);
            }
            else {
                note = transaction.getType().getName();
            }

            String money;
            if (transaction.getType().getId() == 2){
                money = "-" + transaction.getMoney().toString() + " " + currency;
            }
            else if (transaction.getType().getId() == 3 && transaction.getAccountSender().getId() == account.getId()) {
                money = "-" + transaction.getMoney().toString() + " " + currency;
            }
            else {
                money = transaction.getMoney().toString() + " " + currency;
            }
            transactionLines.append(transactionDate)
                    .append(" | ").append(note).append(" ".repeat(50 - note.length()))
                    .append(" | ").append(money).append("\n");
        }
        String transactionLine = transactionLines.toString();
        if (transactionLine.length() == 0){
            transactionLine = "Список транзакций " +
                    "за период с " + getStringDate(dateStart, 2) +
                    " по " + getStringDate(dateEnd, 2) +
                    " пуст\n";
        }

        // result document statement-money
        String resultSet = accountDataSet(account, dateStart, dateEnd) + transactionLine;


        if (createNewDirectory("statement") == -1){
            System.out.println("Error when creating a directory");
        }
        stringToPdfFile(resultSet,"statement/Statement");
        System.out.print(resultSet);
        System.out.println("Выписка сохранена");
    }

    public static void createMoneyReport(Account account, Instant dateStart, Instant dateEnd){
        String currency = account.getCurrency();

        // moneyResult
        BigDecimal moneyReceived = TransactionRepository.getReceivedForAccount(account.getId(), dateStart, dateEnd);
        BigDecimal moneyWithdrawn = TransactionRepository.getWithdrawnForAccount(account.getId(), dateStart, dateEnd);
        String moneyReceivedLine = moneyReceived + " " + currency;
        String moneyWithdrawnLine = "-" + moneyWithdrawn + currency;
        String moneyHeader = " ".repeat(14) + "Приход" + " " + "|" + " Уход\n";
        String line = " ".repeat(10) + "-".repeat(20) + "\n";
        String moneyLine = " ".repeat(20 - moneyReceivedLine.length())  + moneyReceivedLine + " | " + moneyWithdrawnLine + "\n";
        String moneyStatement = moneyHeader + line + moneyLine;

        // result document statement-money
        String resultSet = accountDataSet(account, dateStart, dateEnd) + moneyStatement;

        if (createNewDirectory("statement-money") == -1){
            System.out.println("Error when creating a directory");
        }
        stringToPdfFile(resultSet,"statement-money/MoneyStatement");
        System.out.print(resultSet);
        System.out.println("Выписка сохранена");
    }

    public static void stringToPdfFile(String content, String name)  {
        try {
            final String FONT = "src/main/resources/assets/fonts/couriercyrps.ttf";
            BaseFont baseFont = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont,10,Font.NORMAL);
            Document document = new Document();
            Instant dateTime = Instant.now();
            String time = getStringDate(dateTime, 1);
            String date = getStringTime(dateTime, 2);
            String fileName = name + "_" + time + "_" + date + ".pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            writer.setInitialLeading(12.5f);
            document.open();
            document.add(new Paragraph(content,font));
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String accountDataSet (Account account, Instant dateStart, Instant dateEnd) {
        String currency = account.getCurrency();
        String period = getStringDate(dateStart, 2) + " - " + getStringDate(dateEnd, 2);
        Instant currentDataTime = Instant.now();
        String timeFormation = getStringDate(currentDataTime, 2) + ", "
                + getStringTime(currentDataTime, 3);

        // Common information about account
        String header = " ".repeat(27) + "Выписка\n" + " ".repeat(26) + account.getBank().getName();
        String customerLine = "\nКлиент                    | " + account.getCustomer().getName() + "\n";
        String accountLine = "Счёт                      | " + account.getAccountNumber() + "\n";
        String currencyLine = "Валюта                    | " + account.getCurrency() + "\n";
        String openDateLine = "Дата открытия             | " + account.getOpeningDate().toString() + "\n";
        String periodLine = "Период                    | " + period + "\n";
        String timeFormationLine = "Дата и время формирования | " + timeFormation + "\n";
        String balanceLine = "Остаток                   | " + account.getBalance().toString() + " " + currency + "\n";

        return header +
                customerLine +
                accountLine +
                currencyLine +
                openDateLine +
                periodLine +
                timeFormationLine +
                balanceLine;
    }

    private static String getStringDate (Instant instant, int format){
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        DateTimeFormatter dateFormatter;
        if (format == 1) {
            dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        } else {
            dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        }
        return dateTime.format(dateFormatter);
    }

    private static String getStringTime (Instant instant, int format) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        DateTimeFormatter timeFormatter = switch (format) {
            case 1 -> DateTimeFormatter.ofPattern("HH:mm:ss");
            case 2 -> DateTimeFormatter.ofPattern("HH-mm-ss");
            default -> DateTimeFormatter.ofPattern("HH.mm");
        };

        return dateTime.format(timeFormatter);
    }

    private static String getNoteIfTransactionEqualsTree(Transaction transaction, Account account) {
        if (transaction.getAccountRecipient().getId() == account.getId()) {
            return "Пополнение от ← " + transaction.getAccountSender().getCustomer().getName();
        } else if (transaction.getAccountSender().getId() == account.getId()) {
            return "Перевод средств → " + transaction.getAccountRecipient().getCustomer().getName();
        }
        return "";
    }

    private static int createNewDirectory (String path){
        File directory = new File(path);

        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                return 1;
            }
            else {
                return -1;
            }
        }
        else {
            return 0;
        }
    }
}
