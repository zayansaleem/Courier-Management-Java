📦 Courier Management & Tracking System (Java, Swing, MySQL)

A desktop application built using **Java (Swing)** and **MySQL** to manage courier bookings, tracking, employees, and user accounts.

---

## ✅ Features
✔ Book courier with details (sender, receiver, weight, address, etc.)  
✔ Auto-generated tracking ID  
✔ Track courier status using tracking number  
✔ Manage user accounts (registration + login verification)  
✔ Employee details update  
✔ **History Module** – shows all past bookings of a user  
✔ Stores all records in MySQL database

---

## 🧰 Technologies Used
- Java (Swing UI)
- MySQL Database
- JDBC Connector
- IntelliJ IDEA

---

## 📂 Project Structure

---

## 🗄 Database Tables
**users**
| Column | Type |
|--------|------|
| user_id | INT |
| name | VARCHAR |
| address | VARCHAR |
| phone | VARCHAR |

**courier**
| Column | Type |
|--------|------|
| tracking_id | VARCHAR |
| user_id | INT |
| sender | VARCHAR |
| receiver | VARCHAR |
| status | VARCHAR |
| weight | DOUBLE |

**history**
| Column | Type |
|--------|------|
| user_id | INT |
| tracking_id | VARCHAR |
| status | VARCHAR |
| date | DATETIME |

---

## ▶ How to Run
1. Import project in **IntelliJ**
2. Install MySQL and create database
3. Update DB username/password inside code
4. Run `MainDashboard.java`
5. UI will open — select modules using buttons

---

## ✅ Screenshots (optional)
Add screenshots if you want (makes GitHub look more professional)

---

## 📌 Future Improvements
- Email/SMS notifications for status updates
- Live GPS tracking
- Admin dashboard

---

## 👤 Author
**Zayan Saleem**
B.Tech CSE - Semester 3  
Jyothi Engineering College
