document.addEventListener("DOMContentLoaded", function () {
    const searchForm = document.getElementById("searchForm");
    searchForm.addEventListener("submit", function (event) {
        event.preventDefault(); // Previne o comportamento padrão de submissão do formulário

        const formData = new FormData(searchForm);
        const queryString = new URLSearchParams(formData).toString();

        fetch(`/systemUser-list?${queryString}`, {
            method: "GET",
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            }
        })
            .then(response => response.text())
            .then(data => {
                const parser = new DOMParser();
                const htmlDocument = parser.parseFromString(data, "text/html");
                const newTableBody = htmlDocument.querySelector("#systemUserDatatable tbody");
                const oldTableBody = document.querySelector("#systemUserDatatable tbody");
                oldTableBody.parentNode.replaceChild(newTableBody, oldTableBody);
            })
            .catch(error => console.error('Error:', error));
    });
});

