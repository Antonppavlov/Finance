package ru.barmaglot.android.myfinance.dao.decotation;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.barmaglot.android.myfinance.dao.interfaces.IOperationDAO;
import ru.barmaglot.android.myfinance.exception.AmountException;
import ru.barmaglot.android.myfinance.exception.CurrencyException;
import ru.barmaglot.android.myfinance.objects.impl.operation.ConvertOperation;
import ru.barmaglot.android.myfinance.objects.impl.operation.IncomeOperation;
import ru.barmaglot.android.myfinance.objects.impl.operation.OutcomeOperation;
import ru.barmaglot.android.myfinance.objects.impl.operation.TransferOperation;
import ru.barmaglot.android.myfinance.objects.interfaces.operation.IOperation;
import ru.barmaglot.android.myfinance.objects.interfaces.storage.IStorage;
import ru.barmaglot.android.myfinance.objects.type.OperationType;

import static ru.barmaglot.android.myfinance.objects.type.OperationType.CONVERT;
import static ru.barmaglot.android.myfinance.objects.type.OperationType.INCOME;
import static ru.barmaglot.android.myfinance.objects.type.OperationType.OUTCOME;
import static ru.barmaglot.android.myfinance.objects.type.OperationType.TRANSFER;

public class OperationSynchronizer implements IOperationDAO {

    private List<IOperation> operationList;
    private Map<OperationType, List<IOperation>> operationMap = new EnumMap<>(OperationType.class);//разделяет по типу операции
    private Map<Long, IOperation> identityMap = new HashMap<>();// хранит все источники без учета их уровня в дереве //получение по id

    private SourceSynchronizer sourceSynchronizer;
    private StorageSynchronizer storageSynchronizer;
    private IOperationDAO iOperationDAO;

    public OperationSynchronizer(SourceSynchronizer sourceSynchronizer, StorageSynchronizer storageSynchronizer, IOperationDAO iOperationDAO) {
        this.sourceSynchronizer = sourceSynchronizer;
        this.storageSynchronizer = storageSynchronizer;
        this.iOperationDAO = iOperationDAO;

        init();
    }

    private void init() {
        operationList = iOperationDAO.getAll();

        for (IOperation iOperation : operationList) {
            identityMap.put(iOperation.getId(), iOperation);
        }
        fillOperationMap();
    }

    private void fillOperationMap() {
        List<IOperation> incomeList = new ArrayList<>();
        List<IOperation> outcomeList = new ArrayList<>();
        List<IOperation> transferList = new ArrayList<>();
        List<IOperation> convertList = new ArrayList<>();

        for (IOperation iOperation : operationList) {
            switch (iOperation.getOperationType()) {
                case INCOME: {
                    incomeList.add(iOperation);
                    break;
                }
                case OUTCOME: {
                    outcomeList.add(iOperation);
                    break;
                }
                case TRANSFER: {
                    transferList.add(iOperation);
                    break;
                }
                case CONVERT: {
                    convertList.add(iOperation);
                    break;
                }
            }
        }

        operationMap.put(INCOME, incomeList);
        operationMap.put(OUTCOME, outcomeList);
        operationMap.put(TRANSFER, transferList);
        operationMap.put(CONVERT, convertList);
    }


    @Override
    public List<IOperation> getList(OperationType operationType) {
        return operationMap.get(operationType);
    }

    @Override
    public List<IOperation> getAll() {
        return operationList;
    }

    @Override
    public IOperation get(long id) {
        return identityMap.get(id);
    }

    @Override
    // При добавлении операции – нужно сначала добавить запись в БД, затем добавить новую операцию во все коллекции и обновить баланс соотв. хранилища
    public boolean add(IOperation operation) throws CurrencyException, AmountException {
        if (iOperationDAO.add(operation)) {// если в БД добавился нормально
            addToCollections(operation);// добавляем в коллекции

            boolean updateAmountResult = false;

            try {

                // в зависимости от типа операции - обновляем баланс
                switch (operation.getOperationType()) {
                    case INCOME: { // доход

                        IncomeOperation incomeOperation = (IncomeOperation) operation;

                        BigDecimal currentAmount = incomeOperation.getToStorage().getAmount(incomeOperation.getFromCurrency());// получаем текущее значение остатка (баланса)
                        BigDecimal newAmount = currentAmount.add(incomeOperation.getFromAmount()); //прибавляем сумму операции

                        // обновляем баланс
                        updateAmountResult = storageSynchronizer.updateAmount(incomeOperation.getToStorage(), incomeOperation.getFromCurrency(), newAmount);

                        break;// не забываем ставить break, чтобы следующие case не выполнялись

                    }
                    case OUTCOME: { // расход

                        OutcomeOperation outcomeOperation = (OutcomeOperation) operation;

                        BigDecimal currentAmount = outcomeOperation.getFromStorage().getAmount(outcomeOperation.getFromCurrency());// получаем текущее значение остатка (баланса)
                        BigDecimal newAmount = currentAmount.subtract(outcomeOperation.getFromAmount()); //отнимаем сумму операции

                        // обновляем баланс
                        updateAmountResult = storageSynchronizer.updateAmount(outcomeOperation.getFromStorage(), outcomeOperation.getFromCurrency(), newAmount);


                        break;
                    }

                    case TRANSFER: { // перевод в одной валюте между хранилищами

                        TransferOperation trasnferOperation = (TransferOperation) operation;

                        // для хранилища, откуда перевели деньги - отнимаем сумму операции
                        BigDecimal currentAmountFromStorage = trasnferOperation.getFromStorage().getAmount(trasnferOperation.getFromCurrency());// получаем текущее значение остатка (баланса)
                        BigDecimal newAmountFromStorage = currentAmountFromStorage.subtract(trasnferOperation.getFromAmount()); //отнимаем сумму операции

                        // для хранилища, куда перевели деньги - прибавляем сумму операции
                        BigDecimal currentAmountToStorage = trasnferOperation.getToStorage().getAmount(trasnferOperation.getFromCurrency());// получаем текущее значение остатка (баланса)
                        BigDecimal newAmountToStorage = currentAmountToStorage.add(trasnferOperation.getFromAmount()); //прибавляем сумму операции

                        // обновляем баланс в обоих хранилищах
                        updateAmountResult = storageSynchronizer.updateAmount(trasnferOperation.getFromStorage(), trasnferOperation.getFromCurrency(), newAmountFromStorage) &&
                                storageSynchronizer.updateAmount(trasnferOperation.getToStorage(), trasnferOperation.getFromCurrency(), newAmountToStorage);// для успешного результата - оба обновления должны вернуть true

                        break;

                    }

                    case CONVERT: { // конвертация из любой валюты в любую между хранилищами
                        ConvertOperation convertOperation = (ConvertOperation) operation;

                        // для хранилища, откуда перевели деньги - отнимаем сумму операции
                        BigDecimal currentAmountFromStorage = convertOperation.getFromStorage().getAmount(convertOperation.getFromCurrency());// получаем текущее значение остатка (баланса)
                        BigDecimal newAmountFromStorage = currentAmountFromStorage.subtract(convertOperation.getFromAmount()); // сколько отнимаем

                        // для хранилища, куда перевели деньги - прибавляем сумму операции
                        BigDecimal currentAmountToStorage = convertOperation.getToStorage().getAmount(convertOperation.getToCurrency());// получаем текущее значение остатка (баланса)
                        BigDecimal newAmountToStorage = currentAmountToStorage.add(convertOperation.getToAmount()); // сколько прибавляем


                        // обновляем баланс в обоих хранилищах
                        updateAmountResult = storageSynchronizer.updateAmount(convertOperation.getFromStorage(), convertOperation.getFromCurrency(), newAmountFromStorage) &&
                                storageSynchronizer.updateAmount(convertOperation.getToStorage(), convertOperation.getToCurrency(), newAmountToStorage);// для успешного результата - оба обновления должны вернуть true

                        break;
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!updateAmountResult) {
                delete(operation);// откатываем созданную операцию
                return false;
            }

            return true;

        }
        return false;
    }

    @Override
    public boolean update(IOperation object) throws CurrencyException, AmountException {
        return delete(iOperationDAO.get(object.getId())) && add(object);
    }


    @Override
    public boolean delete(IOperation operation) throws AmountException, CurrencyException {
        boolean delete = iOperationDAO.delete(operation) && revertBalance(operation);
        if (delete) {
            removeFromCollections(operation);
        }
        return delete;
    }

    private boolean revertBalance(IOperation operation) throws CurrencyException, AmountException {
        boolean result = false;

        switch (operation.getOperationType()) {
            case INCOME: {
                //если был доход тогда убавляем сумму с хранилища
                IncomeOperation incomeOperation = (IncomeOperation) operation;

                IStorage toStorage = incomeOperation.getToStorage();
                Currency fromCurrency = incomeOperation.getFromCurrency();
                BigDecimal subtractAmount = toStorage.getAmount(fromCurrency).subtract(incomeOperation.getFromAmount());

                result = storageSynchronizer.updateAmount(
                        toStorage,
                        fromCurrency,
                        subtractAmount
                );

                break;
            }
            case OUTCOME: {
                //если расход то добавляем сумму в хранилище
                OutcomeOperation outcomeOperation = (OutcomeOperation) operation;
                IStorage fromStorage = outcomeOperation.getFromStorage();
                Currency fromCurrency = outcomeOperation.getFromCurrency();

                BigDecimal addAmount = fromStorage.getAmount(fromCurrency).add(outcomeOperation.getFromAmount());

                result = storageSynchronizer.updateAmount(
                        fromStorage,
                        fromCurrency,
                        addAmount
                );

                break;
            }
            case TRANSFER: {
                //если трансфер то возвращаем с одного хранилища в другое
                TransferOperation transferOperation = (TransferOperation) operation;
                IStorage toStorage = transferOperation.getToStorage();
                IStorage fromStorage = transferOperation.getFromStorage();
                Currency fromCurrency = transferOperation.getFromCurrency();
                BigDecimal fromAmount = transferOperation.getFromAmount();

                BigDecimal amountToStorage = toStorage.getAmount(fromCurrency).add(fromAmount);
                BigDecimal amountFromStorage = fromStorage.getAmount(fromCurrency).subtract(fromAmount);

                result = storageSynchronizer.updateAmount(fromStorage, fromCurrency, amountToStorage) &&
                        storageSynchronizer.updateAmount(toStorage, fromCurrency, amountFromStorage);

                break;
            }
            case CONVERT: {
                //то возвращаем с одного типа валюты в другой
                ConvertOperation convertOperation = (ConvertOperation) operation;
                IStorage toStorage = convertOperation.getToStorage();
                IStorage fromStorage = convertOperation.getFromStorage();

                Currency toCurrency = convertOperation.getToCurrency();
                Currency fromCurrency = convertOperation.getFromCurrency();

                BigDecimal toAmount = convertOperation.getToAmount();
                BigDecimal fromAmount = convertOperation.getFromAmount();

                BigDecimal amountToStorage = toStorage.getAmount(toCurrency).add(toAmount);
                BigDecimal amountFromStorage = fromStorage.getAmount(fromCurrency).add(fromAmount);


                result = storageSynchronizer.updateAmount(toStorage, toCurrency, amountToStorage) &&
                        storageSynchronizer.updateAmount(fromStorage, fromCurrency, amountFromStorage);

                break;
            }
        }

        return result;
    }

    private void removeFromCollections(IOperation object) {
        operationList.remove(object);
        operationMap.get(object.getOperationType()).remove(object);
        identityMap.remove(object.getId());
    }


    private void addToCollections(IOperation object) {
        operationList.add(object);
        operationMap.get(object.getOperationType()).add(object);
        identityMap.put(object.getId(), object);
    }

    public IOperationDAO getiOperationDAO() {
        return iOperationDAO;
    }

    public SourceSynchronizer getSourceSynchronizer() {
        return sourceSynchronizer;
    }

    public StorageSynchronizer getStorageSynchronizer() {
        return storageSynchronizer;
    }

    public List<IOperation> getOperationList() {
        return operationList;
    }

    public Map<OperationType, List<IOperation>> getOperationMap() {
        return operationMap;
    }

    public Map<Long, IOperation> getIdentityMap() {
        return identityMap;
    }
}
