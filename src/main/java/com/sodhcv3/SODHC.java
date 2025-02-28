package com.sodhcv3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class SODHC extends Application {

    private static boolean sistemaLigado = false; // Flag para verificar se o sistema está ligado

    // Função para imprimir "café" utilizando os valores ASCII
    public static void imprimirCafe(TextArea textArea) {
        char[] cafe = {69, 85, 32, 84, 69, 32, 65, 77, 79, 32, 3}; 
        StringBuilder cafeString = new StringBuilder();
        for (char c : cafe) {
            cafeString.append(c);
        }
        textArea.appendText("Resposta: " + cafeString.toString() + "\n\n");
    }

    // Função que exibe a saudação com base na hora
    public static void exibirSaudacao(TextArea textArea) {
        Date hora = new Date();
        String horaStr = new SimpleDateFormat("HH:mm:ss").format(hora);
        
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

    public static class WolframAlphaAPI {
        private static final String APP_ID = "HVAJX6-W865HGVL3H";

        public static CompletableFuture<String> consultaWolfram(String query) {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://api.wolframalpha.com/v2/query?input=" + encodedQuery + "&appid=" + APP_ID + "&output=json";

            return CompletableFuture.supplyAsync(() -> {
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    HttpGet request = new HttpGet(url);
                    HttpResponse response = httpClient.execute(request);
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    return parseResposta(jsonResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Erro ao consultar a API do Wolfram Alpha. Tente novamente mais tarde.";
                }
            });
        }

        private static String parseResposta(String jsonResponse) {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            if (!jsonObject.getJSONObject("queryresult").getBoolean("success")) {
                return "Nenhuma resposta relevante encontrada.";
            }

            JSONArray pods = jsonObject.getJSONObject("queryresult").optJSONArray("pods");
            if (pods == null) {
                return "Nenhuma informação disponível.";
            }

            StringBuilder resultado = new StringBuilder();
            for (int i = 0; i < pods.length(); i++) {
                JSONObject pod = pods.getJSONObject(i);
                JSONArray subpods = pod.optJSONArray("subpods");
                if (subpods != null) {
                    resultado.append(pod.getString("title")).append(": ");
                    for (int j = 0; j < subpods.length(); j++) {
                        resultado.append(subpods.getJSONObject(j).optString("plaintext", "Sem resposta")).append("\n");
                    }
                }
            }
            return resultado.toString().isEmpty() ? "Sem respostas disponíveis." : resultado.toString();
        }
    }

    public static void responder(String pergunta, TextArea textArea) {
        pergunta = pergunta.toLowerCase().trim();

        if (pergunta.contains("on") || pergunta.contains("iniciar")) {
            sistemaLigado = true;
            simularCarregamento(); // Carregamento no terminal
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
        } else if (pergunta.contains("wolfram")) {
            String consulta = pergunta.replace("wolfram", "").trim();
            WolframAlphaAPI.consultaWolfram(consulta).thenAccept(resposta -> {
                textArea.appendText("Pergunta: " + consulta + "\nResposta: " + resposta + "\n\n");
            }).exceptionally(e -> {
                textArea.appendText("Erro ao consultar Wolfram Alpha: " + e.getMessage() + "\n");
                return null;
            });
        } else {
            textArea.appendText("Pergunta: " + pergunta + "\nResposta: Desculpe, não entendi a sua pergunta.\n\n");
        }
    }

    // Função para simular carregamento no terminal
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
        
        // Ação do botão ao clicar
        button.setOnAction(e -> {
            String pergunta = textField.getText();
            responder(pergunta, textArea);
            textField.clear();
        });
        
        // Adicionando a ação para pressionar Enter no TextField
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
