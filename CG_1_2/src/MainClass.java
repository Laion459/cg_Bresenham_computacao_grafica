import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * Ponto de entrada da aplicação de Computação Gráfica.
 * Cria a janela Swing e inicia o canvas de renderização.
 */
public class MainClass {

	/**
	 * Inicializa a janela principal e o loop de renderização.
	 *
	 * @param args argumentos de linha de comando (não utilizados)
	 */
	public static void main(String[] args) {
		MainCanvas meuCanvas = new MainCanvas();
		
		JFrame f = new JFrame();
		f.setSize(640, 480);
		f.setVisible(true);
		f.getContentPane().add(meuCanvas);
		
	
		f.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e) {
		        System.exit(0);
		    }
		});
		
		meuCanvas.start();
	}
}
