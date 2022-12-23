/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import static controller.ControllerBD.connection;
import static controller.LanchoneteDAO.file_relatorio_lanchonete_fonte;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import javax.swing.JOptionPane;
import model.Pedido;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;


/**
 *
 * @author Luan Riechi
 */
public class PedidoDAO extends ControllerBD{
    private static final String sqlpesquisar = "SELECT * FROM pedidos WHERE id = ?";
    private static final String sqlregistrar = "INSERT INTO pedidos (id, lanche, adicional, preco) VALUES (?, ?, ?, ?)";
    private static final String sqleditar = "UPDATE pedidos SET lanche = ?, adicional = ?, preco = ? WHERE id = ?";
    private static final String sqlexcluir = "DELETE FROM pedidos WHERE id = ?";

    public static final String pasta_relatorios = System.getProperty("user.dir") + "/relatorios/";
    public static final File file_relatorio_pedido_fonte = new File(pasta_relatorios, "RelatorioPedido.jrxml");
    
    public boolean Registrar(Pedido ped) {
        try {
            int tipo = ResultSet.TYPE_SCROLL_SENSITIVE;
            int concorrencia = ResultSet.CONCUR_UPDATABLE;
            pstdados = connection.prepareStatement(sqlregistrar, tipo, concorrencia);
            pstdados.setInt(1, ped.getId());
            pstdados.setString(2, ped.getLanche());
            pstdados.setString(3, ped.getAdicional());
            pstdados.setFloat(4, ped.getPreco());

            int resposta = pstdados.executeUpdate();
            pstdados.close();
            //DEBUG
            System.out.println("Resposta da inserção = " + resposta);
            //FIM-DEBUG
            if (resposta == 1) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
        } catch (SQLException erro) {
                JOptionPane.showMessageDialog(null,
                "ID ja cadastrado no sistema ou invalido",
                "Erro na execução da inserção",
                JOptionPane.ERROR_MESSAGE);
                JOptionPane.showMessageDialog(null,
                "CLique no botão pesquisar para que o sistema continue funcionando normalmente",
                "Pesquisar inicia uma nova conexão",
                JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Erro na execução da inserção = " + erro);
        }
        return false;
    }
    public boolean Excluir(int id) {
        try {
            int tipo = ResultSet.TYPE_SCROLL_SENSITIVE;
            int concorrencia = ResultSet.CONCUR_UPDATABLE;
            pstdados = connection.prepareStatement(sqlexcluir, tipo, concorrencia);
            pstdados.setInt(1, id);
            int resposta = pstdados.executeUpdate();
            pstdados.close();
            //DEBUG
            System.out.println("Resposta da exclusão = " + resposta);
            //FIM-DEBUG
            if (resposta == 1) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
        } catch (SQLException erro) {
                JOptionPane.showMessageDialog(null,
                "Pedido nao existe!",
                "Erro na execução da exclusão",
                JOptionPane.INFORMATION_MESSAGE);                        
            System.out.println("Erro na execução da exclusão = " + erro);
        }
        return false;
    }
    public boolean Pesquisar(int id) {
        try {
            int tipo = ResultSet.TYPE_SCROLL_SENSITIVE;
            int concorrencia = ResultSet.CONCUR_UPDATABLE;
            pstdados = connection.prepareStatement(sqlpesquisar, tipo, concorrencia);
            pstdados.setInt(1, id);
            rsdados = pstdados.executeQuery();
            rsdados.next();
            return true;
        } catch (SQLException erro) {
                JOptionPane.showMessageDialog(null,
                "Pedido nao existe!",
                "Erro ao executar consulta",
                JOptionPane.INFORMATION_MESSAGE);            
            System.out.println("Erro ao executar consulta = " + erro);
        }
        return false;
    }
     
    public boolean Editar(Pedido ped) {
       try {
            int tipo = ResultSet.TYPE_SCROLL_SENSITIVE;
            int concorrencia = ResultSet.CONCUR_UPDATABLE;
            pstdados = connection.prepareStatement(sqleditar, tipo, concorrencia);
            pstdados.setString(1, ped.getLanche());
            pstdados.setString(2, ped.getAdicional());
            pstdados.setFloat(3, ped.getPreco());
            pstdados.setInt(4, ped.getId());
            int resposta = pstdados.executeUpdate();
            pstdados.close();
            //DEBUG
            System.out.println("Resposta da atualização = " + resposta);
            //FIM-DEBUG
            if (resposta == 1) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
        } catch (SQLException erro) {
                JOptionPane.showMessageDialog(null,
                "Pedido nao existe!",
                "Erro na execução da atualização",
                JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Erro na execução da atualização = " + erro);
        }
        return false;
    }
     
    public Pedido getPedido() {
        Pedido ped = null;
        if (rsdados != null) {
            try {
                int id = rsdados.getInt("id");                
                String lanche = rsdados.getString("lanche");
                String adicional = rsdados.getString("adicional");
                float preco = rsdados.getFloat("preco");
                ped = new Pedido(id, lanche, adicional, preco);
            } catch (SQLException erro) {
                JOptionPane.showMessageDialog(null,
                "Pedido nao existe!",
                "Erro!",
                JOptionPane.INFORMATION_MESSAGE);
                System.out.println(erro);
            }
        }
        return ped;
    }
    public void VisualizarRelatorio(Map parametro){
        JasperPrint impressao;
        try{
            FileInputStream arquivo = new FileInputStream(file_relatorio_pedido_fonte);
            JasperReport relatorio = JasperCompileManager.compileReport(arquivo);
            impressao = JasperFillManager.fillReport(
                    relatorio,//arquivo jasper
                    parametro,//parametros
                    connection);//conexao
            JasperViewer.viewReport(impressao, false);
        }catch (JRException | FileNotFoundException erro) {
            JOptionPane.showMessageDialog(null,
                "Não foi possível visualizar o relatório.!",
                "Erro na visualização",
                JOptionPane.ERROR_MESSAGE);
                System.err.println(" não foi possível visualizar o relatório.\n\n" + erro);
        }
    }
    
    public void ExportarRelatorio(Map parametro){
        JasperPrint impressao;
        try{
            FileInputStream arquivo = new FileInputStream(file_relatorio_pedido_fonte);
            JasperReport relatorio = JasperCompileManager.compileReport(arquivo);
            impressao = JasperFillManager.fillReport(
                    relatorio,//arquivo jasper
                    parametro,//parametros
                    connection);//conexao
            JasperExportManager.exportReportToPdfFile(impressao, "relatorio_Pedido_pdf");
            JOptionPane.showMessageDialog(null, "O arquivo foi gerado com sucesso na pasta do projeto." );
        }catch (JRException | FileNotFoundException erro) {
            JOptionPane.showMessageDialog(null,
                "Não foi possível exportar o relatório.!",
                "Erro na Exportação",
                JOptionPane.ERROR_MESSAGE);
                System.err.println(" não foi possível exportar o relatório.\n\n" + erro);

        }
    }    
}
