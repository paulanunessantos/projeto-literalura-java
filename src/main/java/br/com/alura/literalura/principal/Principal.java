package br.com.alura.literalura.principal;

import br.com.alura.literalura.model.*;
import br.com.alura.literalura.repository.AutorRepository;
import br.com.alura.literalura.repository.LivroRepository;
import br.com.alura.literalura.service.ConsumoApi;
import br.com.alura.literalura.service.ConverteDados;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {

    private LivroRepository livroRepository;
    private AutorRepository autorRepository;

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "http://gutendex.com/books";

    public Principal(LivroRepository livroRepository, AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    ------------------------------------------------
                    Escolha o número de sua opção:
                    1 - Buscar livro pelo título
                    2 - Listar livros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos em um determinado ano
                    5 - Listar livros em um determinado idioma
                    0 - Sair
                    ------------------------------------------------
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarLivroPeloTitulo();
                    break;
                case 2:
                    listarLivrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEmDeterminadoAno();
                    break;
                case 5:
                    listarLivrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarLivroPeloTitulo() {
        System.out.println("Digite o nome do livro para busca:");
        var nomeLivro = leitura.nextLine();
        var json = consumo.obterDados
                (ENDERECO + "?search=" + nomeLivro.replace(" ", "%20"));
        var dadosLivro = converterResultadosEPegarPrimeiroLivro(json);
        Optional<Autor> autor = autorRepository.findByNome(dadosLivro.dadosAutores()
                .getFirst().nome());

        Livro livro = new Livro(dadosLivro);

        if (autor.isPresent()) {
            livro.setAutor(autor.get());
        } else {
            Autor novoAutor = new Autor(dadosLivro.dadosAutores().getFirst());
            autorRepository.save(novoAutor);
            livro.setAutor(novoAutor);
        }

        livroRepository.save(livro);
        System.out.println(livro);
    }

    private List<Livro> listarLivrosRegistrados() {
        List<Livro> livros = livroRepository.findAll();
        livros.forEach(System.out::println);
        return livros;
    }

    private List<Autor> listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();
        autores.forEach(System.out::println);
        return autores;
    }

    private List<Autor> listarAutoresVivosEmDeterminadoAno() {
        System.out.println("Digite o ano em que você deseja verificar quais atores estavam vivos:");
        var ano = leitura.nextInt();
        leitura.nextLine();
        List<Autor> autoresVivos = autorRepository.listarAutoresVivosEmDeterminadoAno(ano);
        autoresVivos.forEach(System.out::println);
        return autoresVivos;
    }

    private void listarLivrosPorIdioma() {
        System.out.println("""
                Insira o idioma para realizar a busca:
                es - espanhol
                en - inglês
                fr - francês
                pt - português
                """);
        var escolhaIdioma = leitura.nextLine();
        List<Livro> livrosEncontrados = livroRepository.findByIdiomaContains(escolhaIdioma);
        livrosEncontrados.forEach(System.out::println);
        if (livrosEncontrados.isEmpty()) {
            System.out.println("Não existem livros nesse idoma em nosso banco de dados.");
        }
    }

    private DadosLivro converterResultadosEPegarPrimeiroLivro(String json) {
        Results results = conversor.obterDados(json, Results.class);
        return results.dadosLivros().getFirst();
    }
}
