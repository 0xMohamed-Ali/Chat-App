# 🗨️ Java Multi-Client Chat Application

A fully functional multi-client chat system built using **pure Java**, demonstrating :

- Java Socket Programming
- Multi-threaded Server using Runnable Interface
- Real-Time User List Synchronization
- Custom Lightweight Communication Protocol
- Swing-Based Modern GUI (No external dependencies)

---

## 🚀 Features

✅ Multi-threaded server handling multiple clients concurrently  
✅ Custom protocol to manage chat messages and user list  
✅ Real-time online user list synchronization  
✅ Modern Java Swing GUI for chat and user management  
✅ Thread-safe shared state management  
✅ Clean architecture and easily extendable code

---

## ⚙️ Architecture Overview

### Server-Side

- Accepts multiple client connections using `ServerSocket`
- Handles each client on a separate thread (`Runnable`)
- Manages shared state using `synchronized` collections
- Broadcasts chat messages and updates user list in real-time
- Custom protocol with prefixes (`MSG:`, `USERS:`) for message parsing

### Client-Side

- Swing-based UI with chat area, message input, and online user list (`JList`)
- Handles server messages using background thread (`IncomingReader`)
- Parses server protocol messages to update chat and user list dynamically
- Simple user authentication via username entry

---

## 🧰 Technologies Used

- Java Core
- Java Sockets
- Java Threads (Runnable Interface)
- Java IO Streams
- Java Swing

---

## 🏗️ Project Structure

```bash
.
├── ChatServer.java  # Multi-threaded server logic
└── ChatClient.java  # Swing-based client logic
