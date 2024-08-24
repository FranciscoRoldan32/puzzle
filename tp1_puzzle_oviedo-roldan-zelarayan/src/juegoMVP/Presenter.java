package juegoMVP;

public class Presenter {

    private Juego juego;
    private Interfaz vista;

    public Presenter(Juego juego, Interfaz vista) {
        this.juego = juego;
        this.vista = vista;
    }

    public void iniciarJuego() {
        if (vista.getBoardPanel() != null) {
            juego.inicializar(vista.getBoardPanel(), vista); // Pasar la instancia actual de Interfaz
            vista.updateBoardPanel(); // Asegúrate de que el panel se actualice
            updateMovimientos();
            vista.setMovimientosText(String.valueOf(juego.getMovimientos())); // Actualizar el conteo de movimientos
        }
    }

    private void updateMovimientos() {
        vista.setMovimientosText(Integer.toString(juego.getMovimientos()));
    }
}
