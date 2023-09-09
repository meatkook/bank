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

/**
 * This class represents a Statement and provides methods to create transaction checks, reports, and money reports.
 */
public class Statement {

    /**
     * Creates a transaction check to console and save it as pdf file for the given transaction.
     *
     * @param transaction The transaction for which the check is created.
     */
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

    /**
     * Creates a report to console and save it as pdf file
     * for the given account within the specified date range.
     *
     * @param account    The account for which the report is created.
     * @param dateStart  The start date of the report.
     * @param dateEnd    The end date of the report.
     */
    public static void createReport(Account account, Instant dateStart, Instant dateEnd){
        String currency = account.getCurrency();

        // transactions list
        StringBuilder transactionLines = new StringBuilder();
        List<Transaction> transactions = TransactionRepository.readPeriodTransactionsOfAccount(account.getId(), dateStart, dateEnd);
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

    /**
     * Creates a money report to console and save it as pdf file
     * for the given account within the specified date range.
     *
     * @param account    The account for which the money report is created.
     * @param dateStart  The start date of the money report.
     * @param dateEnd    The end date of the money report.
     */
    public static void createMoneyReport(Account account, Instant dateStart, Instant dateEnd){
        String currency = account.getCurrency();

        // moneyResult
        BigDecimal moneyReceived = TransactionRepository.getReceivedMoneyForAccount(account.getId(), dateStart, dateEnd);
        BigDecimal moneyWithdrawn = TransactionRepository.getWithdrawnMoneyForAccount(account.getId(), dateStart, dateEnd);
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

    /**
     * Converts the given content to a PDF file with the specified name.
     *
     * @param content The content to be converted to a PDF file.
     * @param name    The name of the PDF file.
     */
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

    /**
     * Returns a formatted string representation of the account data set,
     * including common information about the account,
     * such as customer name, account number, currency, opening date,
     * period, time of formation, and balance.
     *
     * @param account    The account for which the data set is created.
     * @param dateStart  The start date of the period.
     * @param dateEnd    The end date of the period.
     * @return The formatted string representation of the account data set.
     */
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

    /**
     * Returns a formatted string representation of the given instant based on the specified format.
     *
     * @param instant The instant to be formatted.
     * @param format  The format of the string representation (1 for "dd-MM-yyyy", 2 for "dd.MM.yyyy").
     * @return The formatted string representation of the instant.
     */
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

    /**
     * Returns a formatted string representation of the given instant based on the specified format.
     *
     * @param instant The instant to be formatted.
     * @param format  The format of the string representation (1 for "HH:mm:ss", 2 for "HH-mm-ss", 3 for "HH.mm").
     * @return The formatted string representation of the instant.
     */
    private static String getStringTime (Instant instant, int format) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        DateTimeFormatter timeFormatter = switch (format) {
            case 1 -> DateTimeFormatter.ofPattern("HH:mm:ss");
            case 2 -> DateTimeFormatter.ofPattern("HH-mm-ss");
            default -> DateTimeFormatter.ofPattern("HH.mm");
        };

        return dateTime.format(timeFormatter);
    }

    /**
     * Returns a note for the given transaction if its type is equal to 3 and the account matches.
     * If the transaction is a deposit to the account,
     * the note will be "Пополнение от ← [Sender's Customer Name]".
     * If the transaction is a transfer from the account,
     * the note will be "Перевод средств → [Recipient's Customer Name]".
     * If no match is found, an empty string is returned.
     *
     * @param transaction The transaction to check.
     * @param account     The account to match against.
     * @return The note for the transaction, or an empty string if no match is found.
     */
    private static String getNoteIfTransactionEqualsTree(Transaction transaction, Account account) {
        if (transaction.getAccountRecipient().getId() == account.getId()) {
            return "Пополнение от ← " + transaction.getAccountSender().getCustomer().getName();
        } else if (transaction.getAccountSender().getId() == account.getId()) {
            return "Перевод средств → " + transaction.getAccountRecipient().getCustomer().getName();
        }
        return "";
    }

    /**
     * Creates a new directory at the specified path.
     *
     * @param path The path of the directory to create.
     * @return 1 if the directory was created successfully, 0 if it already exists, -1 if an error occurred.
     */
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
