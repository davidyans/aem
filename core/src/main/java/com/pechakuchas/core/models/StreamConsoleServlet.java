package com.pechakuchas.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.framework.Constants;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import javax.servlet.Servlet;
import java.io.*;

@Component(
        service = { Servlet.class },
        property = {
                Constants.SERVICE_DESCRIPTION + "=Java Stream Console Servlet",
                "sling.servlet.methods=POST",
                "sling.servlet.paths=/bin/streamconsole/execute"
        }
)
public class StreamConsoleServlet extends SlingAllMethodsServlet {

    @SuppressWarnings("deprecation")
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String streamCode = request.getParameter("code");
        String result;

        try {
            result = executeStreamCode(streamCode);
        } catch (Exception e) {
            result = "Error: " + e.getMessage();
        }

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("result", result);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }

    private String executeStreamCode(String code) {
        String className = "StreamExecutor";
        String sourceFile = className + ".java";

        String classCode = "import java.util.*; import java.util.stream.*; import java.util.stream.Collectors; public class StreamExecutor {"
                + " public static void main(String[] args) {"
                + " List<String> baseList = Arrays.asList(\"one\", \"two\", \"three\", \"four\", \"five\");"
                + code
                + " } }";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
            writer.write(classCode);
        } catch (IOException e) {
            return "Error escribiendo el archivo: " + e.getMessage();
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int compilationResult = compiler.run(null, null, null, sourceFile);
        if (compilationResult != 0) {
            return "Error en la compilación del código.";
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("java", className);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            return output.toString();
        } catch (IOException e) {
            return "Error ejecutando el código: " + e.getMessage();
        }
    }
}
