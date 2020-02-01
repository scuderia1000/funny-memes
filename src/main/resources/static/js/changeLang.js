/**
 * Created by vvershov on 31.01.2020
 */
var select = document.getElementById("locales");
select.onchange = function(el) {
    debugger
    var selectedOption = el.currentTarget.value;
    // var selectedOption = $('#locales').val();
    if (selectedOption !== ''){
        window.location.replace('?lang=' + selectedOption);
    }
};