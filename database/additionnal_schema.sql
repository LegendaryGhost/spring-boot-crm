CREATE TABLE temp_users
(
    id        INT AUTO_INCREMENT,
    email     VARCHAR(255),
    hire_date VARCHAR(255),
    username  VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE budgets
(
    budget_id   INT AUTO_INCREMENT,
    budget_name VARCHAR(255)   NOT NULL DEFAULT '',
    amount      DECIMAL(15, 2) NOT NULL,
    created_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    customer_id INT UNSIGNED   NOT NULL,
    PRIMARY KEY (budget_id),
    FOREIGN KEY (customer_id) REFERENCES customer (customer_id)
);

CREATE TABLE expenses
(
    expense_id  INT AUTO_INCREMENT,
    amount      DECIMAL(15, 2) NOT NULL,
    description TEXT,
    created_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    lead_id     INT UNSIGNED,
    ticket_id   INT UNSIGNED,
    PRIMARY KEY (expense_id),
    UNIQUE (lead_id),
    UNIQUE (ticket_id),
    FOREIGN KEY (lead_id) REFERENCES trigger_lead (lead_id),
    FOREIGN KEY (ticket_id) REFERENCES trigger_ticket (ticket_id)
);

CREATE TABLE configuration
(
    configuration_key   VARCHAR(255),
    configuration_value VARCHAR(255) NOT NULL,
    PRIMARY KEY (configuration_key)
);
