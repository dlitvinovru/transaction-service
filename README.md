# Transaction Service

В приложении реализовано два класса сервиса для проведения операций со счетами. Первый класс реализует синхронизацию 
на уровне базы данных - `SyncDBTransactionServiceImpl`. Второй на уровне приложения - `SyncJavaTransactionServiceImpl`, 
стоит отметить что данная реализация не поддерживает синхронизацию, если приложение работает на нескольких инстансах, 
в этом случае стоит использовать `SyncDBTransactionServiceImpl`. 
Не была реализованная безопасность приложения, так как из ТЗ не понятно как авторизовывать пользователя и какие права у него есть.
Тестировал вручную.
Хотелось бы еще знать, в каком формате приходят числа для операций со счетами. Сделал ограничение до двух знаков после запятой.

## REST API

### TransactionController

(Сделал методы GET для удобства тестирования)
#### /transaction/put - GET
http://localhost:8080/transaction/put?accountId=1&amount=100

#### /transaction/withdraw - GET
http://localhost:8080/transaction/withdraw?accountId=2&amount=100

#### /transaction/transfer - GET
http://localhost:8080/transaction/transfer?accountFrom=2&accountTo=1&amount=100

#### DB init
- ID BALANCE  
- 1	100.00
- 2	200.00
- 3	300.00
