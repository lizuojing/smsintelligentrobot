package org.app.intelligentrobot.entity;

public class AskKeyWordEntity {

	public String question;
	public String answer;

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String toString() {
		return " 问: " + question + "\n" + " 答:" + answer;
	}
}
