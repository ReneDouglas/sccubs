document.addEventListener("DOMContentLoaded", function() {
    const searchForm = document.getElementById("searchForm");
    const pageSizeSelect = document.getElementById("pageSize");
    const clearButton = document.getElementById("clearButton");
    let currentPage = 0; // Use currentPage instead of page

    function updateTable(queryString) {
        fetch(`/systemUser-list?${queryString}`, {
            method: "GET",
            headers: {
                "X-Requested-With": "searchRequest"
            }
        })
            .then(response => response.text())
            .then(data => {
                const parser = new DOMParser();
                const htmlDocument = parser.parseFromString(data, "text/html");
                replaceElementContent("#systemUserDatatable tbody", htmlDocument.querySelector("#systemUserDatatable tbody"));
                replaceElementContent("#pagination", htmlDocument.querySelector("#pagination"));
                updatePagination(htmlDocument); // Update pagination state (optional)
            })
            .catch(error => console.error('Error:', error));
    }

    function addEventListenersToSelects() {
        document.querySelectorAll("#systemUserDatatable select").forEach(select => {
            select.addEventListener("change", function() {
                // Atualiza os valores no formulário sem enviar AJAX
                const formData = new FormData(searchForm);
                const queryString = new URLSearchParams(formData).toString();
                const pageSize = pageSizeSelect.value;

                // Aqui atualizamos o queryString com o valor atual do formulário
                updateFormValues(queryString, pageSize);
            });
        });
    }

    function replaceElementContent(selector, newContent) {
        const element = document.querySelector(selector);
        if (element) {
            element.parentNode.replaceChild(newContent, element);
        }
    }

    function updatePagination(htmlDocument) {
        const currentPageElement = htmlDocument.getElementById("currentPage");
        if (currentPageElement) {
            currentPage = parseInt(currentPageElement.textContent) - 1; // Adjust for 0-based indexing
        }
    }

    function updateFormValues(formData, queryString) {
        const urlParams = new URLSearchParams(queryString);
        for (const [key, value] of urlParams.entries()) {
            formData.append(key, value);
        }
    }

    function submitSearch(event) {
        event.preventDefault();

        const formData = new FormData(searchForm);
        const queryString = new URLSearchParams(formData).toString();
        const pageSize = pageSizeSelect.value;

        updateTable(`${queryString}&size=${pageSize}&page=${currentPage}`);
    }

    searchForm.addEventListener("submit", submitSearch);

    pageSizeSelect.addEventListener("change", function(event) {
        currentPage = 0; // Reset page on page size change
        submitSearch(event);
    });

    window.goToPreviousPage = function(event) {
        event.preventDefault();
        const prevPageLink = document.getElementById("prevPage");
        if (prevPageLink.hasAttribute("disabled")) return;

        currentPage--;
        submitSearch(event);
    };

    window.goToNextPage = function(event) {
        event.preventDefault();
        const nextPageLink = document.getElementById("nextPage");
        if (nextPageLink.hasAttribute("disabled")) return;

        currentPage++;
        submitSearch(event);
    };

    clearButton.addEventListener("click", function() {
        searchForm.reset();
        const formData = new FormData(searchForm);
        const queryString = new URLSearchParams(formData).toString();
        const pageSize = pageSizeSelect.value;

        updateTable(`size=${pageSize}&page=0`);
    });

    addEventListenersToSelects(); // Add listeners initially
});