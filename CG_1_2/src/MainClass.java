import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Ponto de entrada da aplicação de Computação Gráfica.
 * Cria a janela Swing e inicia o canvas de renderização.
 */
public class MainClass {

	/**
	 * Inicializa a janela principal e o loop de renderização.
	 * Interação a interação:
	 * 1) Cria o {@link MainCanvas}
	 * 2) Monta o {@link JFrame} e adiciona o canvas ANTES de exibir
	 * 3) Exibe a janela
	 * 4) Pede o foco de teclado para o canvas (senão W/Q/E não chegam)
	 * 5) Inicia a thread de desenho
	 *
	 * @param args argumentos de linha de comando (não utilizados)
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			// Interação 1: canvas com framebuffer + listeners
			MainCanvas meuCanvas = new MainCanvas();

			// Interação 2: janela; canvas entra no content pane antes do setVisible
			JFrame f = new JFrame("CG_1_2  Triângulo 3D + Matriz 4x4");
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setSize(920, 740);
			f.getContentPane().add(meuCanvas);

			f.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});

			// Interação 3: mostra a janela
			f.setVisible(true);

			// Interação 4: foco no canvas  obrigatório para KeyListener (W, Q, E, Z…)
			meuCanvas.requestFocusInWindow();

			// Interação 5: loop de renderização
			meuCanvas.start();
		});
	}
}
