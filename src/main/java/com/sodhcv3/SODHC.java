package com.sodhcv3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SODHC extends Application {

    public static String responder(String pergunta) {
        pergunta = pergunta.toLowerCase();

        // Frases pré-definidas com palavras-chave
        if (pergunta.contains("ola") || pergunta.contains("oi")) {
            return "Olá! Como posso te ajudar?";
        } else if (pergunta.contains("o que e regressao linear")) {
            return "A regressão linear é um algoritmo de Machine Learning utilizado para prever valores contínuos com base em dados históricos.";
        } else if (pergunta.contains("oque é uma inteligencia artificial")) {
            return "A inteligência artificial é uma área da ciência da computação que visa criar máquinas capazes de simular o comportamento humano e aprender com dados.";
        } else if (pergunta.contains("qual e o seu nome")) {
            return "Eu sou S.O.D.H.C. System Of Decadence Human in Centipede!";
        } else if (pergunta.contains("previsao") || pergunta.contains("futuro")) {
            return realizarPrevisao();
        } else if (pergunta.contains("saude")) {
            return "A saúde é importante! Lembre-se de se alimentar bem, praticar exercícios e consultar um médico regularmente.";
        } else if (pergunta.contains("clima") || pergunta.contains("tempo")) {
            return "Desculpe, a consulta ao clima está temporariamente desativada.";
        } else if (pergunta.contains("iniciar") || pergunta.contains("on")) {
            simularCarregamento(); // Simulando carregamento
            return "Sistema iniciado!";
        } else if (pergunta.contains("off") || pergunta.contains("desligar")) {
            return "Sistema finalizado com sucesso.";
        } else {
            return "Desculpe, não entendi a sua pergunta.";
        }
    }

    // Função para simular o carregamento
    public static void simularCarregamento() {
        String[] animacoes = {".", "..", "...", "...."};
        System.out.print("Iniciando");

        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(500);  // Pausa de 500ms entre as animações
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print("\rIniciando" + animacoes[i % animacoes.length]);
        }
        System.out.println();
    }

    // Função para realizar os cálculos de regressão linear
    public static String realizarPrevisao() {
        // Simulação de regressão
        return "Previsão baseada em regressão linear (simulação).";
    }

    @Override
    public void start(Stage primaryStage) {
        // Layout principal
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 10;");

        // Caixa de texto para a entrada do usuário
        TextField textField = new TextField();
        textField.setPromptText("Digite sua pergunta...");

        // Botão para enviar a pergunta
        Button button = new Button("Enviar");
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);

        // Quando o botão for clicado, processa a pergunta
        button.setOnAction(e -> {
            String pergunta = textField.getText();
            String resposta = responder(pergunta);
            textArea.appendText("Pergunta: " + pergunta + "\nResposta: " + resposta + "\n\n");
            textField.clear(); // Limpa o campo de texto após enviar
        });

        // Adiciona os componentes no layout
        root.getChildren().addAll(textField, button, textArea);

        // Cria e exibe a cena
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("S.O.D.H.C. - Sistema de Decadência Humana");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Inicia a aplicação JavaFX
        launch(args);
    }
}
