$(document).ready(function () {

    let isBackspace = false; // Variável para rastrear o estado da tecla Backspace

    // Detecta se o Backspace está sendo pressionado
    $('#phoneNumber').on('keydown', function (e) {
        isBackspace = e.key === 'Backspace';
    });

    $('#phoneNumber').on('input', function () {
        let input = $(this).val().replace(/\D/g, ''); // Remove caracteres não numéricos
        let formatted = '';
        const dddInserted = input.length > 2; // Verifica se o DDD já foi digitado


        console.log(isBackspace)
        // Adiciona o DDD (##)
        if (input.length >= 2 && !isBackspace) {
            formatted += '(' + input.substring(0, 2) + ') ';
            input = input.substring(2);
        }

        // Formata os próximos 4 números
        if (input.length > 0) {
            formatted += input.substring(0, 5); // Inclui o 9 junto com os próximos números
            input = input.substring(5);
        }

        // Adiciona o hífen e os últimos 4 números
        if (input.length > 0) {
            formatted += '-' + input.substring(0, 4);
        }

        $(this).val(formatted); // Atualiza o valor formatado no input
    });
});
