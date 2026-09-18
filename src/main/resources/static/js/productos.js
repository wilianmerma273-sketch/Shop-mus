const buscador = document.getElementById("searchInput");

buscador.addEventListener("keyup", () => {

    const texto = buscador.value.toLowerCase();

    const productos = document.querySelectorAll(".producto-card");

    productos.forEach(producto => {

        const nombre = producto.querySelector("h3")
            .textContent
            .toLowerCase();

        if(nombre.includes(texto)){
            producto.style.display = "block";
        }else{
            producto.style.display = "none";
        }

    });

});