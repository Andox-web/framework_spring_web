package mg.itu.prom16.exception;

import java.io.PrintWriter;

public class ErrorPrinter {
    
    // Méthode qui génère et écrit les détails de l'exception au format HTML dans un PrintWriter
    public static void printExceptionHtml(PrintWriter writer, Exception exception) {
        // Commencer le document HTML
        writer.println("<!DOCTYPE html>");
        writer.println("<html lang=\"fr\">");
        writer.println("<head>");
        writer.println("    <meta charset=\"UTF-8\">");
        writer.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        writer.println("    <title>"+exception.getClass().getName()+"</title>");
        writer.println("    <link rel=\"stylesheet\" href=\"styles.css\">");
        writer.println("    <style>");
        writer.println("        body {");
        writer.println("            font-family: Arial, sans-serif;");
        writer.println("            background-color: #f4f4f4;");
        writer.println("            margin: 0;");
        writer.println("            padding: 0;");
        writer.println("        }");
        writer.println("        .container {");
        writer.println("            max-width: 800px;");
        writer.println("            margin: 50px auto;");
        writer.println("            padding: 20px;");
        writer.println("            background-color: #fff;");
        writer.println("            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);");
        writer.println("        }");
        writer.println("        h1 {");
        writer.println("            color: #333;");
        writer.println("        }");
        writer.println("        .exception-details {");
        writer.println("            margin-top: 20px;");
        writer.println("        }");
        writer.println("        .exception-details p {");
        writer.println("            font-size: 16px;");
        writer.println("            color: #555;");
        writer.println("        }");
        writer.println("        pre {");
        writer.println("            background-color: #f9f9f9;");
        writer.println("            padding: 10px;");
        writer.println("            border: 1px solid #ddd;");
        writer.println("            overflow-x: auto;");
        writer.println("        }");
        writer.println("    </style>");
        writer.println("</head>");
        writer.println("<body>");
        writer.println("    <div class=\"container\">");
        writer.println("        <h1>Détails de l'Exception</h1>");
        writer.println("        <div class=\"exception-details\">");
        
        // Afficher le nom de la classe de l'exception (le "code" de l'exception)
        writer.println("            <p><strong>Nom de l'Exception:</strong> " + exception.getClass().getName() + "</p>");
        
        // Afficher le message de l'exception
        writer.println("            <p><strong>Message de l'Exception:</strong> " + exception.getMessage() + "</p>");
        
        // Afficher la trace de la pile
        writer.println("            <p><strong>Trace de la Pile:</strong></p>");
        writer.println("            <pre>");
        
        // Ajouter la trace de la pile d'exécution (stack trace)
        for (StackTraceElement element : exception.getStackTrace()) {
            writer.println("\tat " + element);
        }
        
        // Fermer les balises HTML
        writer.println("            </pre>");
        writer.println("        </div>");
        writer.println("    </div>");
        writer.println("</body>");
        writer.println("</html>");
    }
}
