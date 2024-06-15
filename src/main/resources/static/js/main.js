document.addEventListener('DOMContentLoaded', function () {
    const headers = document.querySelectorAll('.accordion-header');

    headers.forEach(header => {
        header.addEventListener('click', function () {
            const content = this.nextElementSibling;
            const arrow = header.querySelector('.arrow')

            if (content.style.height && content.style.height !== '0px') {
                content.style.height = '0';
                content.style.overflow = 'hidden';
            } else {
                content.style.height = content.scrollHeight + 'px';
                content.style.overflow = 'visible';
            }
            arrow.classList.toggle('rotate-180')
        });
    });
});

function closeAlert(event){
    let element = event.target;
    while(element.nodeName !== "BUTTON"){
        element = element.parentNode;
    }
    element.parentNode.parentNode.removeChild(element.parentNode);
}
