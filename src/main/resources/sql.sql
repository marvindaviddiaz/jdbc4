CREATE TABLE SANDBOX.JDBC4_PRODUCTS (
    ID INTEGER NOT NULL,
    VERSION INTEGER,
    DESCRIPTION VARCHAR(255),
    PRICE DECIMAL(15, 2)
);

ALTER TABLE SANDBOX.JDBC4_PRODUCTS ADD PRIMARY KEY(ID);

INSERT INTO SANDBOX.JDBC4_PRODUCTS VALUES(1,0,'Pepsi-Cola 12 onz', 3.50);
INSERT INTO SANDBOX.JDBC4_PRODUCTS VALUES(2,0,'Cola-Cola 12 onz', 4.50);
INSERT INTO SANDBOX.JDBC4_PRODUCTS VALUES(3,0,'7up 12 onz', 5.50);
