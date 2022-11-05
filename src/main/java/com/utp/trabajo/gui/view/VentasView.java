package com.utp.trabajo.gui.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.utp.trabajo.model.entities.services.util.IconService;
import com.utp.trabajo.model.entities.services.util.UtilService;
import com.utp.trabajo.model.entities.services.VentasService;
import javax.annotation.PostConstruct;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VentasView extends javax.swing.JInternalFrame {

	@Autowired
	private VentasService ventasService;

	@Autowired
	private IconService iconos;

	@Autowired
	private UtilService utilidades;
		
	public VentasView() {	
		initComponents();
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				tabbedPaneChangeListener(e);
			}
			
		});
	}
        
	@PostConstruct
	private void init(){
		setFrameIcon(iconos.iconoVentas);
	}
	
	private void tabbedPaneChangeListener(ChangeEvent e) {
		int selectedIndex = tabbedPane.getSelectedIndex();
		System.out.println(selectedIndex);
		if(selectedIndex == 1) {
			listaVentasTable.setModel(ventasService.listarVentas()); 
			//TODO: redo it with the swing worker
		}
	}
	
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        flatTable1 = new com.formdev.flatlaf.extras.components.FlatTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        listaVentasTable = new com.formdev.flatlaf.extras.components.FlatTable();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Ventas");

        flatTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(flatTable1);

        tabbedPane.addTab("Nueva venta", jScrollPane1);

        listaVentasTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(listaVentasTable);

        tabbedPane.addTab("Lista de ventas", jScrollPane2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.formdev.flatlaf.extras.components.FlatTable flatTable1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private com.formdev.flatlaf.extras.components.FlatTable listaVentasTable;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
