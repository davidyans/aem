document.querySelector('.copy').addEventListener('click', function () {
    const codeTextarea = document.getElementsByClassName('code');

    // Copia el texto al portapapeles usando la API moderna
    navigator.clipboard.writeText(codeTextarea.value)
        .then(() => {
            alert('Código copiado al portapapeles!');
        })
        .catch(err => {
            console.error('Error al copiar el texto: ', err);
        });
});
