function getProfissionais(idProfissao, idMunicipio)
{
    cancelarAvaliacao();
    if (idProfissao == 0){
        $("#profissionais tbody").empty();
        $("#profissionais_message").text("Selecione uma profissão!");
    } else {
        $("#profissionais_message").text("");
        httpGetAsync("/buscar/profissionais?idProfissao="+idProfissao+"&idMunicipio="+idMunicipio, popularProfissionais);
    }
}

function setMessage(message)
{
    $("#profissionais_message").text(message);
}

function popularProfissionais(profissionais)
{
    $("#profissionais tbody").empty();
    for (var key in profissionais){
        //var contato = "<td><button onclick='alert(\""+profissionais[key].nome+" => "+profissionais[key].contato+"\")'>Contato</button></td>";
        msg = ("Olá, "+profissionais[key].nome+", estou fazendo contato através da plataforma *Eu Indico*, ").replaceAll(" ", "%20");
        fone = profissionais[key].contato.replaceAll(" ","").replaceAll("(","").replaceAll(")","").replaceAll("-","");
        if (!fone.startsWith("+"))
            if (fone.startsWith("55"))
                fone = "+" + fone;
            else 
                fone = "+55" + fone;
        var base_wa = "window.open('https://wa.me/"+fone.replaceAll("+", "%2B")+"?text="+msg+"', 'contato Eu Indico').focus();";
        var contato = "<td><button onclick=\""+base_wa+"\">Conversar</button></td>";
        var avaliar = "<td><button onclick='avaliarProfissional("+profissionais[key].id+","+profissionais[key].idProfissao+", \""+profissionais[key].nome+"\")'>Avaliar</button></td>";
        $("#profissionais").append("<tr value="+profissionais[key].id+"><td>"+profissionais[key].nome+"</td><td>"+profissionais[key].nota+"</td>"+contato+avaliar+"</tr>");
    };
    if (profissionais.length == 0){
        $("#profissionais_message").text("Nenhum profissional encontrado!");
    }
}

function avaliarProfissional(idPrestador, idProfissao, nome)
{
    $("#profissionais_message").text("Avaliar profissional:");
    $('#idProfissao').val(idProfissao);
    $('#idPrestador').val(idPrestador);
    $('#NomePrestador').text(nome);
    $("#comentario").val("")
    $("#divAvaliar").show();
    $("#avaliacoes").val(0);
    $("#divAvaliar").attr("style", "");
}

function confirmarAvaliacao()
{
    var avaliacao = $("#avaliacoes").val();
    if (avaliacao == 0){
        return setMessage("Selecione uma avaliação!")
    }
    payload = {'idProfissao': $('#idProfissao').val(), 'idPrestador': $('#idPrestador').val(), 'avaliacao': avaliacao, 'comentario': $("#comentario").val()}
    url = "/buscar/avaliar";
    httpPostAsync(url, payload, avaliacaoOk, setMessage);
}

function avaliacaoOk()
{
    $("#profissionais_message").text("Avaliação enviada com sucesso!");
    $("#divAvaliar").hide();
    httpGetAsync("/buscar/profissionais?idProfissao="+$('#idProfissao').val(), popularProfissionais);
}

function cancelarAvaliacao()
{
    $("#divAvaliar").hide();
    $("#profissionais_message").text("");
}