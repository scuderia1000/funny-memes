/**
 * Created by vvershov on 31.01.2020
 */
var select = document.getElementById("locales");
select.onchange = function(el) {
    var selectedOption = el.currentTarget.value;
    if (selectedOption !== ''){
        window.location.replace('?lang=' + selectedOption);
    }
};