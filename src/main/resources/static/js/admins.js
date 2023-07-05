$(document).ready(function () {
    let totalPages = 1;
    let sort = "name"
    let dir = "asc";
    let sortName = $('#sortName');
    let sortSurname = $('#sortSurname');
    let sortEmail = $('#sortEmail');

    fetchAdmin(0);

    sortName.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "name";
        fetchAdmin(0);
    });
    sortSurname.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "surname";
        fetchAdmin(0);
    });
    sortEmail.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "email";
        fetchAdmin(0);
    });

    function fetchAdmin(startPage) {

        $.ajax({
            type: "GET",
            url: "/admin/admins",
            data: {
                page: startPage,
                size: 7,
                sort: sort + "," + dir
            },
            success: function (response) {
                $('#adminsTable tbody').empty();
                $.each(response.content, (i, admin) => {
                    let adminRow = '<tr>' +
                        '<td>' + admin.name + '</td>' +
                        '<td>' + admin.surname + '</td>' +
                        '<td>' + admin.email + '</td>' +
                        '<td>' + admin.grantedAdminBy + '</td>' +
                        '<td>' + admin.grantedDate + '</td>' +
                        '</tr>';
                    $('#adminsTable tbody').append(adminRow);
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
            fetchAdmin(0);
            active.removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "»") {
            fetchAdmin(totalPages - 1);
            $("li.active").removeClass("active");
            active.next().addClass("active");

        } else if (val.toUpperCase() === "›") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue < totalPages) {
                let currentActive = active;
                startPage = activeValue;
                fetchAdmin(startPage);
                active.removeClass("active");
                currentActive.next().addClass("active");
            }
        } else if (val.toUpperCase() === "‹") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue > 1) {
                startPage = activeValue - 2;
                fetchAdmin(startPage);
                let currentActive = active;
                currentActive.removeClass("active");
                currentActive.prev().addClass("active");
            }
        } else {
            startPage = parseInt(val - 1);
            fetchAdmin(startPage);
            active.removeClass("active");
            $(this).parent().addClass("active");
        }
    });
});