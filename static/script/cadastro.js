function getCidades(uf)
{
    httpGetAsync("/cadastro/municipios?uf="+uf, popularCidades);
}

function popularCidades(cidades)
{
    $("#cidade").empty();
    for (var key in cidades){
        $("#cidade").append($('<option>', {
            value: cidades[key].id,
            text: cidades[key].nome
        }));
    };
}

function mostraAlterarSenha(){
    $("#secPassword").show();
    $("#alterarSenha").hide();
    $("#changePassword").val("S");
    $("#currentPassword").val("");
}

function escondeAlterarSenha(){
    $("#secPassword").hide();
    $("#alterarSenha").show();
    $("#changePassword").val("N");
}
