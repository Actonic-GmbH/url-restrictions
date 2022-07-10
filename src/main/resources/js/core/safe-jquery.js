define("url-restriction/safe-jquery", [], function () {
    if (window.jQuery) return window.jQuery;
    if (AJS.$) return AJS.$;
    if ($) return $;
});

