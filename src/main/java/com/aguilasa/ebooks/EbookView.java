package com.aguilasa.ebooks;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EbookView {

	private JFrame frame;
	private JPanel panel;
	private JTextField textField;
	private JPanel panel_1;
	private JPanel panel_2;
	private JButton btnPesquisar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EbookView window = new EbookView();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public EbookView() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		frame.getContentPane().add(panel, BorderLayout.PAGE_START);

		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(30);

		btnPesquisar = new JButton("Pesquisar");
		panel.add(btnPesquisar);

		panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.PAGE_END);

		panel_2 = new JPanel();
		frame.getContentPane().add(panel_2, BorderLayout.CENTER);
	}

}
