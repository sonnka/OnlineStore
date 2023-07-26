$(document).ready(function () {
    let totalPages = 1;
    let sort = "name"
    let dir = "asc";
    let sortName = $('#sortName');
    let sortPrice = $('#sortPrice');

    fetchProduct(0);

    sortName.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "name";
        fetchProduct(0);
    });
    sortPrice.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "price";
        fetchProduct(0);
    });

    function fetchProduct(startPage) {
        $.ajax({
            type: "GET",
            url: "/products",
            data: {
                page: startPage,
                size: 10,
                sort: sort + "," + dir
            },
            success: function (response) {
                $('#table tbody').empty();
                $.each(response.content, (i, product) => {
                    let productRow = '<tr>' +
                        '<td><img alt="img" height="60" id="thumbnail" width="60" ' +
                        'src="http://images.example.com/products/' + product.image +
                        '" onerror="this.onerror=null;this.src=\'http://images.example.com/products/default.png\'" ' +
                        '/></td>' +
                        '<td>' + product.name + '</td>' +
                        '<td>' + product.price + '</td>' +
                        '</tr>';
                    $('#table tbody').append(productRow);
                });

                if ($('ul.pagination li').length - 2 !== response.totalPages) {
                    $('ul.pagination').empty();
                    buildPagination(response);
                }
            },
            error: function (e) {
                console.log("ERROR: ", e);
                window.location = "/";
            }
        });
    }

    function buildPagination(response) {
        totalPages = response.totalPages;
        let pageNumber = response.pageable.pageNumber;
        let numLinks = 10;

        let first = '';
        let prev = '';

        if (pageNumber > 0) {
            first = '<li class="page-item"><a class="page-link">«</a></li>';
            prev = '<li class="page-item"><a class="page-link">‹</a></li>';
        } else {
            prev = '';
            first = '';
        }

        let next = '';
        let last = '';

        if (pageNumber < totalPages) {
            if (pageNumber !== totalPages - 1) {
                next = '<li class="page-item"><a class="page-link">›</a></li>';
                last = '<li class="page-item"><a class="page-link">»</a></li>';
            }
        } else {
            next = '';
            last = '';
        }

        let start = pageNumber - (pageNumber % numLinks) + 1;
        let end = start + numLinks - 1;
        end = Math.min(totalPages, end);
        let pagingLink = '';

        for (let i = start; i <= end; i++) {
            if (i === (pageNumber + 1)) {
                pagingLink += '<li class="page-item active"><a class="page-link"> ' + i + ' </a></li>';
            } else {
                pagingLink += '<li class="page-item"><a class="page-link"> ' + i + ' </a></li>';
            }
        }

        pagingLink = first + prev + pagingLink + next + last;

        $("ul.pagination").append(pagingLink);
    }

    $(document).on("click", "ul.pagination li a", function () {
        let val = $(this).text();
        let active = $("li.active");
        console.log('val: ' + val);

        let startPage;
        if (val.toUpperCase() === "«") {
            let currentActive = active;
            fetchProduct(0);
            active.removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "»") {
            let currentActive = active;
            fetchProduct(totalPages - 1);
            active.removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "›") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue < totalPages) {
                let currentActive = active;
                startPage = activeValue;
                fetchProduct(startPage);
                active.removeClass("active");
                currentActive.next().addClass("active");
            }
        } else if (val.toUpperCase() === "‹") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue > 1) {
                startPage = activeValue - 2;
                fetchProduct(startPage);
                let currentActive = active;
                currentActive.removeClass("active");
                currentActive.prev().addClass("active");
            }
        } else {
            startPage = parseInt(val - 1);
            fetchProduct(startPage);
            active.removeClass("active");
            $(this).parent().addClass("active");
        }
    });
});