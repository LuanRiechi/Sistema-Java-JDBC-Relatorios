
package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import model.Cliente;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
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
public class ClienteDAO extends ControllerBD{
    private static final String sqlpesquisar = "SELECT * FROM clientes WHERE cpf = ?";
    private static final String sqlregistrar = "INSERT INTO clientes (idcliente, nome, cpf, email) VALUES (?, ?, ?, ?)";
    private static final String sqleditar = "UPDATE clientes SET idcliente = ?, nome = ?, email = ? WHERE cpf = ?";
    private static final String sqlexcluir = "DELETE FROM clientes WHERE cpf = ?";
    
    public static final String pasta_relatorios = System.getProperty("user.dir") + "/relatorios/";
    public static final File file_relatorio_cliente_fonte = new File(pasta_relatorios, "RelatorioCliente.jrxml");
    
    public boolean Registrar(Cliente cli) {
        try {
            int tipo = ResultSet.TYPE_SCROLL_SENSITIVE;
            int concorrencia = ResultSet.CONCUR_UPDATABLE;
            pstdados = connection.prepareStatement(sqlregistrar, tipo, concorrencia);
            pstdados.setInt(1, cli.getIdcliente());
            pstdados.setString(2, cli.getNome());
            pstdados.setString(3, cli.getCpf());
            pstdados.setString(4, cli.getEmail());

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
                "CPF ja cadastrado no sistema ou invalido",
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
    public boolean Excluir(String cpf) {
        try {
            int tipo = ResultSet.TYPE_SCROLL_SENSITIVE;
            int concorrencia = ResultSet.CONCUR_UPDATABLE;
            pstdados = connection.prepareStatement(sqlexcluir, tipo, concorrencia);
            pstdados.setString(1, cpf);
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
                "Cliente nao existe!",
                "Erro na execução da exclusão",
                JOptionPane.INFORMATION_MESSAGE);                        
            System.out.println("Erro na execução da exclusão = " + erro);
        }
        return false;
    }
    public boolean Pesquisar(String cpf) {
        try {
            int tipo = ResultSet.TYPE_SCROLL_SENSITIVE;
            int concorrencia = ResultSet.CONCUR_UPDATABLE;
            pstdados = connection.prepareStatement(sqlpesquisar, tipo, concorrencia);
            pstdados.setString(1, cpf);
            rsdados = pstdados.executeQuery();
            rsdados.next();
            return true;
        } catch (SQLException erro) {
                JOptionPane.showMessageDialog(null,
                "Cliente nao existe!",
                "Erro ao executar consulta",
                JOptionPane.INFORMATION_MESSAGE);            
            System.out.println("Erro ao executar consulta = " + erro);
        }
        return false;
    }
     
    public boolean Editar(Cliente cli) {
       try {
            int tipo = ResultSet.TYPE_SCROLL_SENSITIVE;
            int concorrencia = ResultSet.CONCUR_UPDATABLE;
            pstdados = connection.prepareStatement(sqleditar, tipo, concorrencia);
            pstdados.setInt(1, cli.getIdcliente());
            pstdados.setString(2, cli.getNome());
            pstdados.setString(3, cli.getEmail());
            pstdados.setString(4, cli.getCpf());
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
                "Cliente nao existe!",
                "Erro na execução da atualização",
                JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Erro na execução da atualização = " + erro);
        }
        return false;
    }
     
    public Cliente getClient() {
        Cliente cli = null;
        if (rsdados != null) {
            try {
                int idcliente = rsdados.getInt("idcliente");                
                String nome = rsdados.getString("nome");
                String cpf = rsdados.getString("cpf");
                String email = rsdados.getString("email");
                cli = new Cliente(idcliente, nome, cpf, email);
            } catch (SQLException erro) {
                JOptionPane.showMessageDialog(null,
                "Cliente nao existe!",
                "Erro!",
                JOptionPane.INFORMATION_MESSAGE);
                System.out.println(erro);
            }
        }
        return cli;
    }
    
    public void VisualizarRelatorio(){
        JasperPrint impressao;
        try{
            FileInputStream arquivo = new FileInputStream(file_relatorio_cliente_fonte);
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
            FileInputStream arquivo = new FileInputStream(file_relatorio_cliente_fonte);
            JasperReport relatorio = JasperCompileManager.compileReport(arquivo);
            impressao = JasperFillManager.fillReport(
                    relatorio,//arquivo jasper
                    null,//parametros
                    connection);//conexao
            JasperExportManager.exportReportToPdfFile(impressao, "relatorio_cliente_pdf");
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
