# 🧵 Java Compiler – Lexical, Syntactic and Semantic Analysis  
Academic project developed in Java for the Compiler Construction course.  
This system performs **lexical, syntactic and semantic analysis**, fully implemented manually **without JFlex, CUP, or any parser generator**.

---

## 🎯 Project Objective
Build a functional compiler in Java that:

- Analyzes source code entered by the user.
- Detects lexical, syntactic, and semantic errors.
- Generates internal structures such as the Symbol Table and the IRD/IDR stacks.
- Provides a graphical user interface (GUI) for demonstration using Java Swing / NetBeans.

---

# 📌 Main Features

### 🔹 **1. Lexical Analysis**
Manually implemented scanner that recognizes:

- Identifiers  
- Reserved keywords  
- Operators  
- Delimiters  
- Numeric literals  
- Error detection with line/column information  

No automated tools are used — everything is coded manually.

---

### 🔹 **2. Syntactic Analysis**
- Implemented using a custom LL(1) / recursive-descent parser.
- Validates:
  - Declarations  
  - Assignments  
  - Expressions  
  - Blocks  
- Reports precise syntax errors.

---

### 🔹 **3. Semantic Analysis**
Fully aligned with the official grading rubric:

✔️ Builds a **Symbol Table**  
✔️ Creates **IRD and IDR stacks** for arithmetic expressions  
✔️ Validates **type compatibility** between variable type and expression result  
✔️ Performs semantic analysis **only on the first arithmetic expression**, as required  
✔️ If no arithmetic expression exists → outputs a message indicating that  

Additional checks include:

- Undeclared variables  
- Duplicate declarations  
- Type mismatches in assignments  

---

# 🗂️ Project Structure

src/
│── controlador/
│ ├── AnalisisLexico.java
│ ├── AnalisisSintactico.java
│ ├── AnalisisSemantico.java
│ ├── Simbolo.java
│ ├── Token.java
│ └── ...
│
│── vista/
│ ├── VentanaPrincipal.form
│ ├── VentanaPrincipal.java
│ └── ...
│
│── modelo/
│ └── ...
│
└── Main.java


Includes:

- `Token` class  
- `Simbolo` (Symbol Table entry)  
- IRD/IDR stacks  
- Independent analyzers  
- GUI interface with buttons:
  - Load file  
  - Lexical analysis  
  - Syntactic analysis  
  - **Semantic analysis**  
  - Clear console  
  - Show Symbol Table  

---

# ▶️ How to Run the Project

### **Option 1: NetBeans (recommended)**
1. Open NetBeans  
2. Go to **File → Open Project**  
3. Select the compiler project folder  
4. Press **Run ▶️**  

### **Option 2: Command Line**
If you prefer manual execution:

```bash
javac -d build src/**/*.java
java -cp build Main
📄 Requirements
Java 8+

NetBeans, IntelliJ or any Java IDE

Swing suppor
