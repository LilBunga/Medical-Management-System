# Medical Management System

A desktop pharmacy/medical-store management application built with Java Swing and MySQL. Designed to help small clinics and pharmacies manage drug inventory, supplier data, sales transactions, and stock warnings from a single interface.

---

## Features

### Dashboard
- Stat cards showing real-time counts: Total Drugs, Companies, and Sales Records
- Welcome card with navigation guide

### Drug Inventory (`mm_drugs`)
- Add, update, and delete drug records
- Fields: Name, Type (Medicine / Syrup), Price, Expiry Days, Company, Shelf No., Quantity
- Record a sale directly from the drug list — automatically deducts stock and inserts a sales record
- Auto-removes drug entry when stock reaches zero after a sale
- Real-time search/filter across all columns
- Export visible rows to CSV

### Company / Supplier (`mm_company`)
- Add, update, and delete supplier records
- Fields: Name, Address, Phone No.
- Real-time search/filter
- Export to CSV

### Sales Transactions (`mm_sales`)
- View all sales records with Today's Revenue and Grand Total stats
- Update or delete individual records
- Date range filter (From / To, format `dd/MM/yyyy`) with validation
- Real-time search/filter
- Export filtered/sorted view to CSV

### Stock & Expiry Warnings (`mm_warning`)
- Auto-populated list of drugs where **Quantity < 15** OR **Expiry Days < 11**
- Updated automatically on every Add, Update, and Delete in the Drugs module
- Red-themed table for immediate visual attention
- Real-time search/filter

### General
- Sidebar navigation with Logout confirmation dialog
- About Us dialog listing development team
- All numeric input fields validate format before database writes
- All SQL queries use `PreparedStatement` (SQL injection prevention)

---

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java SE 8+ |
| UI Framework | Java Swing (JFrame, JTable, JDialog, BoxLayout, BorderLayout, GridBagLayout) |
| Database | MySQL 5.7+ / MariaDB |
| JDBC Driver | mysql-connector-j |
| Build | Manual compilation (javac) or any Java IDE |

---

## Prerequisites

1. **Java Development Kit (JDK)** 8 or higher
2. **MySQL** server running on `localhost:3306`
3. **mysql-connector-j** JAR on the classpath (download from [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/))

---

## Database Setup

Connect to your MySQL server and run the following SQL to create the database and all required tables.

```sql
CREATE DATABASE IF NOT EXISTS medical_management;
USE medical_management;

CREATE TABLE IF NOT EXISTS mm_drugs (
    SN          INT AUTO_INCREMENT PRIMARY KEY,
    Name        VARCHAR(255)    NOT NULL,
    Type        VARCHAR(50)     NOT NULL,
    Price       INT             NOT NULL,
    `Expiry day's` INT          NOT NULL,
    Company     VARCHAR(255)    NOT NULL,
    `Shelf No.` INT             NOT NULL,
    Quantity    INT             NOT NULL
);

CREATE TABLE IF NOT EXISTS mm_company (
    Name        VARCHAR(255)    PRIMARY KEY,
    Address     VARCHAR(255)    NOT NULL,
    `Phone No.` VARCHAR(50)     NOT NULL
);

CREATE TABLE IF NOT EXISTS mm_sales (
    SN          INT AUTO_INCREMENT PRIMARY KEY,
    Name        VARCHAR(255)    NOT NULL,
    Type        VARCHAR(50)     NOT NULL,
    Price       INT             NOT NULL,
    Quantity    INT             NOT NULL,
    `Total Price` INT           NOT NULL,
    Date        VARCHAR(20)     NOT NULL
);

CREATE TABLE IF NOT EXISTS mm_warning (
    Name        VARCHAR(255),
    Type        VARCHAR(50),
    `Expiry day's` INT,
    Quantity    INT
);
```

---

## Configuration

Database connection settings are in `DBConnection.java`:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/medical_management?zeroDateTimeBehavior=CONVERT_TO_NULL";
private static final String USER     = "root";
private static final String PASSWORD = "";
```

Change `USER` and `PASSWORD` to match your MySQL credentials before compiling.

---

## Project Structure

```
Medical-Management-System/
├── main.java               # Entry point — launches Welcome screen
├── Welcome.java            # Welcome/splash screen
├── Login.java              # Login screen with credential check
├── Admin_GUI.java          # Main application frame (sidebar, header, dashboard)
├── Admin_GUI_drugs.java    # Drug inventory module
├── Admin_GUI_company.java  # Company/Supplier module
├── Admin_GUI_sales.java    # Sales transaction module
├── Admin_GUI_warning.java  # Stock & expiry warning module
├── DBConnection.java       # Centralized database connection utility
├── UITheme.java            # Color constants, fonts, and Swing component factories
└── ExportUtils.java        # CSV export utility with Excel BOM support
```

### Key Design Decisions

| File | Role |
|------|------|
| `DBConnection.java` | Single source of truth for DB config — all modules call `DBConnection.getConnection()` |
| `UITheme.java` | All colors, fonts, and factory methods (`createButton`, `applyTableStyle`, `createToolbar`, etc.) — change appearance from one place |
| `ExportUtils.java` | Reusable CSV export that respects the table's current filter/sort state |

---

## How to Compile and Run

### Using an IDE (recommended)

1. Open the project in IntelliJ IDEA, Eclipse, or NetBeans.
2. Add `mysql-connector-j-x.x.x.jar` to the project's classpath / libraries.
3. Run `main.java`.

### Using the command line

```bash
# From the project root directory
javac -cp ".:/path/to/mysql-connector-j.jar" com/project/*.java

java -cp ".:/path/to/mysql-connector-j.jar" com.project.Main
```

> On Windows replace `:` with `;` in the classpath separator.

---

## Default Login

| Field | Value |
|-------|-------|
| Username | `admin` |
| Password | `admin` |

Credentials are defined as constants in `Login.java` and can be changed there.

---

## Workflow Overview

```
Welcome Screen
    └── Login Screen
            └── Admin Dashboard
                    ├── Drugs       → Add / Update / Delete / Record Sale
                    ├── Company     → Add / Update / Delete
                    ├── Sales       → View / Update / Delete / Date Filter / Export
                    └── Warning     → Auto-generated low-stock & near-expiry list
```

When a sale is recorded from the Drugs module:
1. A new row is inserted into `mm_sales` with the computed total price and today's date.
2. The drug's `Quantity` in `mm_drugs` is decremented.
3. If `Quantity` reaches zero, the drug entry is deleted.
4. The `mm_warning` table is refreshed to reflect the new stock levels.

---

## Known Limitations

- **Single user / no authentication system** — credentials are hardcoded constants; there is no user management or role-based access.
- **No pagination** — all table rows are loaded into memory at once; may be slow for very large datasets.
- **Date stored as VARCHAR** — the `Date` column in `mm_sales` is stored as a `dd/MM/yyyy` string, not a native SQL `DATE` type. The date range filter relies on string parsing.
- **Sequential ID renumbering** — after a delete, `SN` values are renumbered using a MySQL user variable (`@num`). This approach is not safe for concurrent multi-user environments.
- **No data backup/restore UI** — use mysqldump or your database tool directly.

