package com.sodhcv3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class SODHC extends Application {

    private static boolean sistemaLigado = false; // Flag para verificar se o sistema está ligado
    private static final String OPENAI_API_KEY = "";
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    // Função para imprimir "café" utilizando os valores ASCII
    public static void imprimirCafe(TextArea textArea) {
        char[] cafe = {69, 85, 32, 84, 69, 32, 65, 68, 77, 73, 82, 79}; 
        StringBuilder cafeString = new StringBuilder();
        for (char c : cafe) {
            cafeString.append(c);
        }
        textArea.appendText("Resposta: " + cafeString.toString() + "\n\n");
    }

    // Função que exibe a saudação com base na hora
    public static void exibirSaudacao(TextArea textArea) {
        Date hora = new Date();
        int horaInt = Integer.parseInt(new SimpleDateFormat("HH").format(hora));
        String result;
        if (horaInt < 12) {
            result = "S.O.D.H.C.: Bom dia senhor, o que faremos hoje?";
        } else if (horaInt < 18) {
            result = "S.O.D.H.C.: Boa tarde senhor, o que faremos hoje?";
        } else {
            result = "S.O.D.H.C.: Boa noite senhor, o que faremos hoje?";
        }

        textArea.appendText(result + "\n\n");
    }

    public static class OpenAIAPI {
        public static CompletableFuture<String> consultaOpenAI(String prompt) {
            return CompletableFuture.supplyAsync(() -> {
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    HttpPost request = new HttpPost(OPENAI_URL);
                    request.addHeader("Authorization", "Bearer " + OPENAI_API_KEY);
                    request.addHeader("Content-Type", "application/json");

                    JSONObject payload = new JSONObject();
                    payload.put("model", "gpt-3.5-turbo");
                    payload.put("messages", new JSONObject[]{
                        new JSONObject().put("role", "user").put("content", prompt)
                    });

                    request.setEntity(new StringEntity(payload.toString(), StandardCharsets.UTF_8));
                    HttpResponse response = httpClient.execute(request);
                    String jsonResponse = EntityUtils.toString(response.getEntity());

                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    return jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Erro ao consultar a API da OpenAI. Tente novamente mais tarde.";
                }
            });
        }
    }

    public static void responder(String pergunta, TextArea textArea) {
        pergunta = pergunta.toLowerCase().trim();

        if (pergunta.contains("on") || pergunta.contains("iniciar")) {
            sistemaLigado = true;
            simularCarregamento();
            textArea.appendText("Sistema iniciado!\n\n");
            exibirSaudacao(textArea);
        } else if (pergunta.contains("off") || pergunta.contains("desligar")) {
            sistemaLigado = false;
            textArea.appendText("Sistema finalizado com sucesso.\n\n");
        } else if (!sistemaLigado) {
            textArea.appendText("Sistema desligado. Digite 'on' para ligar.\n\n");
        } else if (pergunta.contains("ola") || pergunta.contains("oi")) {
            textArea.appendText("Pergunta: " + pergunta + "\nResposta: Olá! Como posso te ajudar?\n\n");
        } else if (pergunta.contains("café")) {
            textArea.appendText("Pergunta: " + pergunta + "\n");
            imprimirCafe(textArea);
        } else {
            OpenAIAPI.consultaOpenAI(pergunta).thenAccept(resposta -> {
                textArea.appendText("Pergunta: "  + "\nResposta: " + resposta + "\n\n");
            }).exceptionally(e -> {
                textArea.appendText("Erro ao consultar OpenAI: " + e.getMessage() + "\n");
                return null;
            });
        }
    }

    public static void simularCarregamento() {
        String[] animacoes = {".", "..", "...", "...."};
        System.out.print("Iniciando");

        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print("\rIniciando" + animacoes[i % animacoes.length]);
        }
        System.out.println();
    }

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 10;");

        TextField textField = new TextField();
        textField.setPromptText("Digite sua pergunta...");

        Button button = new Button("Enviar");
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);

        button.setOnAction(e -> {
            String pergunta = textField.getText();
            responder(pergunta, textArea);
            textField.clear();
        });

        textField.setOnAction(e -> {
            String pergunta = textField.getText();
            responder(pergunta, textArea);
            textField.clear();
        });

        root.getChildren().addAll(textField, button, textArea);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("S.O.D.H.C. - Sistema de Decadência Humana em Centopeia");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
