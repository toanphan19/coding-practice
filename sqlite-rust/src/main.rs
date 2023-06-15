use rustyline::error::ReadlineError;
use rustyline::{DefaultEditor, Result};

fn print_welcome() {
    println!(
        "
SQLite-Rust version 0.0.1
Enter '.help' for usage hints.
Use '.open FILENAME' to reopen on a persistent database.
"
    );
}

fn repl() -> Result<()> {
    // `()` can be used when no completer is required
    let mut rl = DefaultEditor::new()?;
    #[cfg(feature = "with-file-history")]
    if rl.load_history("history.txt").is_err() {
        println!("No previous history.");
    }
    loop {
        let readline = rl.readline(">> ");
        let line: String;
        match readline {
            Ok(l) => {
                rl.add_history_entry(l.as_str())?;
                line = l;
            }
            Err(ReadlineError::Interrupted) => {
                println!("CTRL-C");
                break;
            }
            Err(ReadlineError::Eof) => {
                println!("CTRL-D");
                break;
            }
            Err(err) => {
                println!("Error: {:?}", err);
                break;
            }
        }

        // Handle meta commands
        if line.starts_with('.') {
            match handle_meta_command(line.as_str()) {
                MetaCommandResult::Success => continue,
                MetaCommandResult::Exit => break,
                MetaCommandResult::UnrecognizedCommand => {
                    println!("Unknown command '{}'", line);
                    continue;
                }
            }
        }

        // Handle statements like SELECT and INSERT
        let statement: Statement;
        match prepare_statement(line.as_str()) {
            PrepareResult::Success(s) => statement = s,
            PrepareResult::UnrecognizedStatement => {
                println!("Unrecognized keyword at start of '{}'", line);
                continue;
            } // PrepareResult::SyntaxError => {
              //     println!("Syntax error. Could not parse statement.");
              //     continue;
              // }
        }

        execute_statement(statement);
    }
    #[cfg(feature = "with-file-history")]
    rl.save_history("history.txt");

    println!("Executed.");

    Ok(())
}

enum MetaCommandResult {
    Success,
    Exit,
    UnrecognizedCommand,
}
fn handle_meta_command(line: &str) -> MetaCommandResult {
    match line {
        ".help" => {
            print_help();
            MetaCommandResult::Success
        }
        ".exit" => MetaCommandResult::Exit,
        _ => MetaCommandResult::UnrecognizedCommand,
    }
}

fn print_help() {
    println!(
        ".exit ?CODE?             Exit this program
.help                    Print this usage information
.open ?OPTIONS? ?FILE?   Close existing database and reopen FILE
    "
    );
}

//
enum PrepareResult {
    Success(Statement),
    UnrecognizedStatement,
    // SyntaxError,
}

// Statements codes
enum StatementType {
    Select,
    Insert,
    Update,
    Delete,
}
struct Statement {
    statement_type: StatementType,
}
fn prepare_statement(line: &str) -> PrepareResult {
    if line.to_lowercase().starts_with("select") {
        return PrepareResult::Success(Statement {
            statement_type: StatementType::Select,
        });
    }
    if line.to_lowercase().starts_with("insert") {
        return PrepareResult::Success(Statement {
            statement_type: StatementType::Insert,
        });
    }
    // if line.starts_with("update") {
    //     return PrepareResult::Success(Statement {
    //         statement_type: StatementType::Update,
    //     });
    // }
    // if line.starts_with("delete") {
    //     return PrepareResult::Success(Statement {
    //         statement_type: StatementType::Delete,
    //     });
    // }

    PrepareResult::UnrecognizedStatement
}

fn execute_statement(statement: Statement) {
    match statement.statement_type {
        StatementType::Select => println!("This is where we would do a select."),
        StatementType::Insert => println!("This is where we would do an insert."),
        StatementType::Update => println!("This is where we would do an update."),
        StatementType::Delete => println!("This is where we would do a delete."),
    }
}

fn main() -> Result<()> {
    print_welcome();
    return repl();
}
