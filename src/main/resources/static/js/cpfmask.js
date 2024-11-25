$(document).ready(function() {
    $('#cpf').on('input', function() {
        let value = $(this).val();

        // Remove caracteres não numéricos
        value = value.replace(/\D/g, '');

        // Limita a 11 números
        value = value.substring(0, 11);

        // Aplica a máscara
        if (value.length > 0) {
            value = value.replace(/(\d{3})(\d)/, '$1.$2');
            value = value.replace(/(\d{3})(\d)/, '$1.$2');
            value = value.replace(/(\d{3})(\d{1,2})/, '$1-$2');
        }

        $(this).val(value);
    });

    // Permite apenas números
    $('#cpf').on('keypress', function(e) {
        const char = String.fromCharCode(e.which);
        const value = $(this).val().replace(/\D/g, '');

        // Previne digitação se já tiver 11 números
        if (value.length >= 11) {
            e.preventDefault();
            return;
        }

        // Previne caracteres não numéricos
        if (!/[0-9]/.test(char)) {
            e.preventDefault();
        }
    });

    // Trata colagem de conteúdo
    $('#cpf').on('paste', function(e) {
        e.preventDefault();
        const paste = e.originalEvent.clipboardData.getData('text');

        // Pega apenas os números do conteúdo colado
        let numbers = paste.replace(/\D/g, '');

        // Limita a 11 números
        numbers = numbers.substring(0, 11);

        // Aplica a máscara
        if (numbers.length > 0) {
            numbers = numbers.replace(/(\d{3})(\d)/, '$1.$2')
                .replace(/(\d{3})(\d)/, '$1.$2')
                .replace(/(\d{3})(\d{1,2})/, '$1-$2');
        }

        $(this).val(numbers);
    });
});