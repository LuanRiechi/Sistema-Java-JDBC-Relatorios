/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import static controller.ClienteDAO.file_relatorio_cliente_fonte;
import static controller.ControllerBD.connection;
import static controller.ControllerBD.pstdados;
import static controller.ControllerBD.rsdados;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import model.Lanchonete;
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
public class LanchoneteDAO extends ControllerBD{
    private static final String sqlpesquisar = "SELECT * FROM lanchonetes WHERE cnpj = ?";
    private static final String sqlregistrar = "INSERT INTO lanchonetes (cnpj, nome, numerofunc) VALUES (?, ?, ?)";
    private static final String sqleditar = "UPDATE lanchonetes SET nome = ?, numerofunc = ? WHERE cnpj = ?";
    private static final String sqlexcluir = "DELETE FROM lanchonetes WHERE cnpj = ?";
    
    public static final String pasta_relatorios = System.getProperty("user.dir") + "/relatorios/";
    public static final File file_relatorio_lanchonete_fonte = new File(pasta_relatorios, "RelatorioLanchonete.jrxml");
    
    public boolean Registrar(Lanchonete lan) {
        try {
            int tipo = ResultSet.TYPE_SCROLL_SENSITIVE;
            int concorrencia = ResultSet.CONCUR_UPDATABLE;
            pstdados = connection.prepareStatement(sqlregistrar, tipo, concorrencia);
            pstdados.setString(1, lan.getCnpj());
            pstdados.setString(2, lan.getNome());
            pstdados.setInt(3, lan.getNumerofunc());


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
                "CNPJ ja cadastrado no sistema ou invalido",
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
    public boolean Excluir(String cnpj) {
        try {
            int tipo = ResultSet.TYPE_SCROLL_SENSITIVE;
            int concorrencia = ResultSet.CONCUR_UPDATABLE;
            pstdados = connection.prepareStatement(sqlexcluir, tipo, concorrencia);
            pstdados.setString(1, cnpj);
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
                "Lanchonete nao existe!",
                "Erro na execução da exclusão",
                JOptionPane.INFORMATION_MESSAGE);                        
            System.out.println("Erro na execução da exclusão = " + erro);
        }
        return false;
    }
    public boolean Pesquisar(String cnpj) {
        try {
            int tipo = ResultSet.TYPE_SCROLL_SENSITIVE;
            int concorrencia = ResultSet.CONCUR_UPDATABLE;
            pstdados = connection.prepareStatement(sqlpesquisar, tipo, concorrencia);
            pstdados.setString(1, cnpj);
            rsdados = pstdados.executeQuery();
            rsdados.next();
            return true;
        } catch (SQLException erro) {
                JOptionPane.showMessageDialog(null,
                "Lanchonete nao existe!",
                "Erro ao executar consulta",
                JOptionPane.INFORMATION_MESSAGE);            
            System.out.println("Erro ao executar consulta = " + erro);
        }
        return false;
    }
     
    public boolean Editar(Lanchonete lan) {
       try {
            int tipo = ResultSet.TYPE_SCROLL_SENSITIVE;
            int concorrencia = ResultSet.CONCUR_UPDATABLE;
            pstdados = connection.prepareStatement(sqleditar, tipo, concorrencia);
            pstdados.setString(1, lan.getNome());
            pstdados.setInt(2, lan.getNumerofunc());
            pstdados.setString(3, lan.getCnpj());            
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
                "Lanchonete nao existe!",
                "Erro na execução da atualização",
                JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Erro na execução da atualização = " + erro);
        }
        return false;
    }
     
    public Lanchonete getLanchonete() {
        Lanchonete lan = null;
        if (rsdados != null) {
            try {
                String cnpj = rsdados.getString("cnpj");                
                String nome = rsdados.getString("nome");
                int numerofunc = rsdados.getInt("numerofunc");
                lan = new Lanchonete(cnpj, nome, numerofunc);
            } catch (SQLException erro) {
                JOptionPane.showMessageDialog(null,
                "Lanchonete nao existe!",
                "Erro!",
                JOptionPane.INFORMATION_MESSAGE);
                System.out.println(erro);
            }
        }
        return lan;
    }
    public void VisualizarRelatorio(){
        JasperPrint impressao;
        try{
            FileInputStream arquivo = new FileInputStream(file_relatorio_lanchonete_fonte);
            JasperReport relatorio = JasperCompileManager.compileReport(arquivo);
            impressao = JasperFillManager.fillReport(
                    relatorio,//arquivo jasper
                    null,//parametros
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
    
    public void ExportarRelatorio(){
        JasperPrint impressao;
        try{
            FileInputStream arquivo = new FileInputStream(file_relatorio_lanchonete_fonte);
            JasperReport relatorio = JasperCompileManager.compileReport(arquivo);
            impressao = JasperFillManager.fillReport(
                    relatorio,//arquivo jasper
                    null,//parametros
                    connection);//conexao
            JasperExportManager.exportReportToPdfFile(impressao, "relatorio_lanchonete_pdf");
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

