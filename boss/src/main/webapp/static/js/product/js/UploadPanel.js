
if (window.SWFUpload) {
    delete window.SWFUpload;
}

var SWFUpload = function (settings) {
    this.initSWFUpload(settings);
};

SWFUpload.prototype.initSWFUpload = function (settings) {
    try {
        this.customSettings = {};	// A container where developers can place their own settings associated with this instance.
        this.settings = settings;
        this.eventQueue = [];
        this.movieName = "SWFUpload_" + SWFUpload.movieCount++;
        this.movieElement = null;
        this.sourceWindowId = "";


        // Setup global control tracking
        SWFUpload.instances[this.movieName] = this;

        // Load the settings.  Load the Flash movie.
        this.initSettings();
        this.loadFlash();
        this.displayDebugInfo();
    } catch (ex) {
        delete SWFUpload.instances[this.movieName];
        console.log(ex);
    }
};

/* *************** */
/* Static Members  */
/* *************** */
SWFUpload.instances = {};
SWFUpload.movieCount = 0;
SWFUpload.version = "2.2.0 Beta 3";
SWFUpload.QUEUE_ERROR = {
    QUEUE_LIMIT_EXCEEDED: -100,
    FILE_EXCEEDS_SIZE_LIMIT: -110,
    ZERO_BYTE_FILE: -120,
    INVALID_FILETYPE: -130
};
SWFUpload.UPLOAD_ERROR = {
    HTTP_ERROR: -200,
    MISSING_UPLOAD_URL: -210,
    IO_ERROR: -220,
    SECURITY_ERROR: -230,
    UPLOAD_LIMIT_EXCEEDED: -240,
    UPLOAD_FAILED: -250,
    SPECIFIED_FILE_ID_NOT_FOUND: -260,
    FILE_VALIDATION_FAILED: -270,
    FILE_CANCELLED: -280,
    UPLOAD_STOPPED: -290
};
SWFUpload.FILE_STATUS = {
    QUEUED: -1,
    IN_PROGRESS: -2,
    ERROR: -3,
    COMPLETE: -4,
    CANCELLED: -5
};
SWFUpload.BUTTON_ACTION = {
    SELECT_FILE: -100,
    SELECT_FILES: -110,
    START_UPLOAD: -120
};
SWFUpload.CURSOR = {
    ARROW: -1,
    HAND: -2
};
SWFUpload.WINDOW_MODE = {
    WINDOW: "window",
    TRANSPARENT: "transparent",
    OPAQUE: "opaque"
};

/* ******************** */
/* Instance Members  */
/* ******************** */

// Private: initSettings ensures that all the
// settings are set, getting a default value if one was not assigned.
SWFUpload.prototype.initSettings = function () {
    this.ensureDefault = function (settingName, defaultValue) {
        this.settings[settingName] = (this.settings[settingName] == undefined) ? defaultValue : this.settings[settingName];
    };

    // Upload backend settings
    this.ensureDefault("source_window_id", "");

    this.ensureDefault("upload_url", "");
    this.ensureDefault("file_post_name", "Filedata");
    this.ensureDefault("use_query_string", false);
    this.ensureDefault("requeue_on_error", false);
    this.ensureDefault("http_success", []);

    // File Settings
    this.ensureDefault("file_types", "*.*");
    this.ensureDefault("file_types_description", "All Files");
    this.ensureDefault("file_size_limit", 0);	// Default zero means "unlimited"
    this.ensureDefault("file_upload_limit", 0);
    this.ensureDefault("file_queue_limit", 0);

    // Flash Settings
    this.ensureDefault("flash_url", "swfupload.swf");
    this.ensureDefault("prevent_swf_caching", true);

    // Button Settings
    this.ensureDefault("button_image_url", "");
    this.ensureDefault("button_width", 1);
    this.ensureDefault("button_height", 1);
    this.ensureDefault("button_text", "");
    this.ensureDefault("button_text_style", "color: #000000; font-size: 16pt;");
    this.ensureDefault("button_text_top_padding", 0);
    this.ensureDefault("button_text_left_padding", 0);
    this.ensureDefault("button_action", SWFUpload.BUTTON_ACTION.SELECT_FILES);
    this.ensureDefault("button_disabled", false);
    this.ensureDefault("button_placeholder_id", "");
    this.ensureDefault("button_cursor", SWFUpload.CURSOR.ARROW);
    this.ensureDefault("button_window_mode", SWFUpload.WINDOW_MODE.WINDOW);

    // Debug Settings
    this.ensureDefault("debug", false);
    this.settings.debug_enabled = this.settings.debug;	// Here to maintain v2 API

    // Event Handlers
    this.settings.return_upload_start_handler = this.returnUploadStart;
    this.ensureDefault("swfupload_loaded_handler", null);
    this.ensureDefault("file_dialog_start_handler", null);
    this.ensureDefault("file_queued_handler", null);
    this.ensureDefault("file_queue_error_handler", null);
    this.ensureDefault("file_dialog_complete_handler", null);

    this.ensureDefault("upload_start_handler", null);
    this.ensureDefault("upload_progress_handler", null);
    this.ensureDefault("upload_error_handler", null);
    this.ensureDefault("upload_success_handler", null);
    this.ensureDefault("upload_complete_handler", null);

    this.ensureDefault("debug_handler", this.debugMessage);

    this.ensureDefault("custom_settings", {});

    // Other settings
    this.customSettings = this.settings.custom_settings;

    this.sourceWindowId = this.settings.source_window_id;

    // Update the flash url if needed
    if (this.settings.prevent_swf_caching) {
        this.settings.flash_url = this.settings.flash_url;// + (this.settings.flash_url.indexOf("?") < 0 ? "?" : "&") + new Date().getTime();
    }

    delete this.ensureDefault;
};

SWFUpload.prototype.loadFlash = function () {
    if (this.settings.button_placeholder_id !== "") {
        this.replaceWithFlash();
    } else {
        this.appendFlash();
    }
};

// Private: appendFlash gets the HTML tag for the Flash
// It then appends the flash to the body
SWFUpload.prototype.appendFlash = function () {
    var targetElement, container;

    // Make sure an element with the ID we are going to use doesn't already exist
    if (document.getElementById(this.movieName) !== null) {
        console.log("ID " + this.movieName + " is already in use. The Flash Object could not be added");
    }

    // Get the body tag where we will be adding the flash movie
    targetElement = document.getElementsByTagName("body")[0];

    if (targetElement == undefined) {
        console.log("Could not find the 'body' element.");
    }

    // Append the container and load the flash
    container = document.createElement("div");
    container.style.width = "1px";
    container.style.height = "1px";
    container.style.overflow = "hidden";

    targetElement.appendChild(container);
    container.innerHTML = this.getFlashHTML();	// Using innerHTML is non-standard but the only sensible way to dynamically add Flash in IE (and maybe other browsers)

    // Fix IE Flash/Form bug
    if (window[this.movieName] == undefined) {
        window[this.movieName] = this.getMovieElement();
    }


};

// Private: replaceWithFlash replaces the button_placeholder element with the flash movie.
SWFUpload.prototype.replaceWithFlash = function () {
    var targetElement, tempParent;

    // Make sure an element with the ID we are going to use doesn't already exist
    if (document.getElementById(this.movieName) !== null) {
        console.log("ID " + this.movieName + " is already in use. The Flash Object could not be added");
    }

    // Get the element where we will be placing the flash movie
    targetElement = document.getElementById(this.settings.button_placeholder_id);

    if (targetElement == undefined) {
        console.log("Could not find the placeholder element: " + this.settings.button_placeholder_id);
    }

    // Append the container and load the flash
    tempParent = document.createElement("div");
    tempParent.innerHTML = this.getFlashHTML();	// Using innerHTML is non-standard but the only sensible way to dynamically add Flash in IE (and maybe other browsers)
    targetElement.parentNode.replaceChild(tempParent.firstChild, targetElement);

    // Fix IE Flash/Form bug
    if (window[this.movieName] == undefined) {
        window[this.movieName] = this.getMovieElement();
    }

};

// Private: getFlashHTML generates the object tag needed to embed the flash in to the document
SWFUpload.prototype.getFlashHTML = function () {
    return ['<object id="', this.movieName, '" type="application/x-shockwave-flash" data="', this.settings.flash_url, '" width="', this.settings.button_width, '" height="', this.settings.button_height, '" class="swfupload">',
        '<param name="wmode" value="', this.settings.button_window_mode, '" />',
        '<param name="movie" value="', this.settings.flash_url, '" />',
        '<param name="quality" value="high" />',
        '<param name="menu" value="false" />',
        '<param name="allowScriptAccess" value="always" />',
        '<param name="flashvars" value="' + this.getFlashVars() + '"/>',
        '</object>'].join("");


};

// Private: getFlashVars builds the parameter string that will be passed
// to flash in the flashvars param.
SWFUpload.prototype.getFlashVars = function () {
    var httpSuccessString = this.settings.http_success.join(",");

    // Build the parameter string
    return ["movieName=", encodeURIComponent(this.movieName),
        "&amp;uploadURL=", encodeURIComponent(this.settings.upload_url),
        "&amp;useQueryString=", encodeURIComponent(this.settings.use_query_string),
        "&amp;requeueOnError=", encodeURIComponent(this.settings.requeue_on_error),
        "&amp;httpSuccess=", encodeURIComponent(httpSuccessString),
        "&amp;filePostName=", encodeURIComponent(this.settings.file_post_name),
        "&amp;fileTypes=", encodeURIComponent(this.settings.file_types),
        "&amp;fileTypesDescription=", encodeURIComponent(this.settings.file_types_description),
        "&amp;fileSizeLimit=", encodeURIComponent(this.settings.file_size_limit),
        "&amp;fileUploadLimit=", encodeURIComponent(this.settings.file_upload_limit),
        "&amp;fileQueueLimit=", encodeURIComponent(this.settings.file_queue_limit),
        "&amp;debugEnabled=", encodeURIComponent(this.settings.debug_enabled),
        "&amp;buttonImageURL=", encodeURIComponent(this.settings.button_image_url),
        "&amp;buttonWidth=", encodeURIComponent(this.settings.button_width),
        "&amp;buttonHeight=", encodeURIComponent(this.settings.button_height),
        "&amp;buttonText=", encodeURIComponent(this.settings.button_text),
        "&amp;buttonTextTopPadding=", encodeURIComponent(this.settings.button_text_top_padding),
        "&amp;buttonTextLeftPadding=", encodeURIComponent(this.settings.button_text_left_padding),
        "&amp;buttonTextStyle=", encodeURIComponent(this.settings.button_text_style),
        "&amp;buttonAction=", encodeURIComponent(this.settings.button_action),
        "&amp;buttonDisabled=", encodeURIComponent(this.settings.button_disabled),
        "&amp;buttonCursor=", encodeURIComponent(this.settings.button_cursor)
    ].join("");
};

// Public: getMovieElement retrieves the DOM reference to the Flash element added by SWFUpload
// The element is cached after the first lookup
SWFUpload.prototype.getMovieElement = function () {
    if (this.movieElement == undefined) {
        this.movieElement = document.getElementById(this.movieName);
    }

    if (this.movieElement === null) {
        console.log("Could not find Flash element");
    }

    return this.movieElement;
};

// Public: Used to remove a SWFUpload instance from the page. This method strives to remove
// all references to the SWF, and other objects so memory is properly freed.
// Returns true if everything was destroyed. Returns a false if a failure occurs leaving SWFUpload in an inconsistant state.
// Credits: Major improvements provided by steffen
SWFUpload.prototype.destroy = function () {
    try {
        // Make sure Flash is done before we try to remove it
        this.cancelUpload(null, false);


        // Remove the SWFUpload DOM nodes
        var movieElement = null;
        movieElement = this.getMovieElement();

        if (movieElement && typeof(movieElement.CallFunction) === "unknown") { // We only want to do this in IE
            // Loop through all the movie's properties and remove all function references (DOM/JS IE 6/7 memory leak workaround)
            for (var i in movieElement) {
                try {
                    if (typeof(movieElement[i]) === "function") {
                        movieElement[i] = null;
                    }
                } catch (ex1) {
                }
            }

            // Remove the Movie Element from the page
            try {
                movieElement.parentNode.removeChild(movieElement);
            } catch (ex) {
            }
        }

        // Remove IE form fix reference
        window[this.movieName] = null;

        // Destroy other references
        SWFUpload.instances[this.movieName] = null;
        delete SWFUpload.instances[this.movieName];

        this.movieElement = null;
        this.settings = null;
        this.customSettings = null;
        this.eventQueue = null;
        this.movieName = null;


        return true;
    } catch (ex2) {
        return false;
    }
};


// Public: displayDebugInfo prints out settings and configuration
// information about this SWFUpload instance.
// This function (and any references to it) can be deleted when placing
// SWFUpload in production.
SWFUpload.prototype.displayDebugInfo = function () {
    this.debug(
        [
            "---SWFUpload Instance Info---\n",
            "Version: ", SWFUpload.version, "\n",
            "Movie Name: ", this.movieName, "\n",
            "Settings:\n",
            "\t", "upload_url:               ", this.settings.upload_url, "\n",
            "\t", "flash_url:                ", this.settings.flash_url, "\n",
            "\t", "use_query_string:         ", this.settings.use_query_string.toString(), "\n",
            "\t", "requeue_on_error:         ", this.settings.requeue_on_error.toString(), "\n",
            "\t", "http_success:             ", this.settings.http_success.join(", "), "\n",
            "\t", "file_post_name:           ", this.settings.file_post_name, "\n",
            "\t", "file_types:               ", this.settings.file_types, "\n",
            "\t", "file_types_description:   ", this.settings.file_types_description, "\n",
            "\t", "file_size_limit:          ", this.settings.file_size_limit, "\n",
            "\t", "file_upload_limit:        ", this.settings.file_upload_limit, "\n",
            "\t", "file_queue_limit:         ", this.settings.file_queue_limit, "\n",
            "\t", "debug:                    ", this.settings.debug.toString(), "\n",

            "\t", "prevent_swf_caching:      ", this.settings.prevent_swf_caching.toString(), "\n",

            "\t", "button_placeholder_id:    ", this.settings.button_placeholder_id.toString(), "\n",
            "\t", "button_image_url:         ", this.settings.button_image_url.toString(), "\n",
            "\t", "button_width:             ", this.settings.button_width.toString(), "\n",
            "\t", "button_height:            ", this.settings.button_height.toString(), "\n",
            "\t", "button_text:              ", this.settings.button_text.toString(), "\n",
            "\t", "button_text_style:        ", this.settings.button_text_style.toString(), "\n",
            "\t", "button_text_top_padding:  ", this.settings.button_text_top_padding.toString(), "\n",
            "\t", "button_text_left_padding: ", this.settings.button_text_left_padding.toString(), "\n",
            "\t", "button_action:            ", this.settings.button_action.toString(), "\n",
            "\t", "button_disabled:          ", this.settings.button_disabled.toString(), "\n",

            "\t", "custom_settings:          ", this.settings.custom_settings.toString(), "\n",
            "Event Handlers:\n",
            "\t", "swfupload_loaded_handler assigned:  ", (typeof this.settings.swfupload_loaded_handler === "function").toString(), "\n",
            "\t", "file_dialog_start_handler assigned: ", (typeof this.settings.file_dialog_start_handler === "function").toString(), "\n",
            "\t", "file_queued_handler assigned:       ", (typeof this.settings.file_queued_handler === "function").toString(), "\n",
            "\t", "file_queue_error_handler assigned:  ", (typeof this.settings.file_queue_error_handler === "function").toString(), "\n",
            "\t", "upload_start_handler assigned:      ", (typeof this.settings.upload_start_handler === "function").toString(), "\n",
            "\t", "upload_progress_handler assigned:   ", (typeof this.settings.upload_progress_handler === "function").toString(), "\n",
            "\t", "upload_error_handler assigned:      ", (typeof this.settings.upload_error_handler === "function").toString(), "\n",
            "\t", "upload_success_handler assigned:    ", (typeof this.settings.upload_success_handler === "function").toString(), "\n",
            "\t", "upload_complete_handler assigned:   ", (typeof this.settings.upload_complete_handler === "function").toString(), "\n",
            "\t", "debug_handler assigned:             ", (typeof this.settings.debug_handler === "function").toString(), "\n"
        ].join("")
    );
};

/* Note: addSetting and getSetting are no longer used by SWFUpload but are included
 the maintain v2 API compatibility
 */
// Public: (Deprecated) addSetting adds a setting value. If the value given is undefined or null then the default_value is used.
SWFUpload.prototype.addSetting = function (name, value, default_value) {
    if (value == undefined) {
        return (this.settings[name] = default_value);
    } else {
        return (this.settings[name] = value);
    }
};

// Public: (Deprecated) getSetting gets a setting. Returns an empty string if the setting was not found.
SWFUpload.prototype.getSetting = function (name) {
    if (this.settings[name] != undefined) {
        return this.settings[name];
    }

    return "";
};


// Private: callFlash handles function calls made to the Flash element.
// Calls are made with a setTimeout for some functions to work around
// bugs in the ExternalInterface library.
SWFUpload.prototype.callFlash = function (functionName, argumentArray) {
    argumentArray = argumentArray || [];

    var movieElement = this.getMovieElement();
    var returnValue, returnString;

    // Flash's method if calling ExternalInterface methods (code adapted from MooTools).
    try {
        returnString = movieElement.CallFunction('<invoke name="' + functionName + '" returntype="javascript">' + __flash__argumentsToXML(argumentArray, 0) + '</invoke>');
        returnValue = eval(returnString);
    } catch (ex) {
        console.log("Call to " + functionName + " failed");
    }

    // Unescape file post param values
    if (returnValue != undefined && typeof returnValue.post === "object") {
        returnValue = this.unescapeFilePostParams(returnValue);
    }

    return returnValue;
};


/* *****************************
 -- Flash control methods --
 Your UI should use these
 to operate SWFUpload
 ***************************** */

// WARNING: this function does not work in Flash Player 10
// Public: selectFile causes a File Selection Dialog window to appear.  This
// dialog only allows 1 file to be selected.
SWFUpload.prototype.selectFile = function () {
    this.callFlash("SelectFile");
};

// WARNING: this function does not work in Flash Player 10
// Public: selectFiles causes a File Selection Dialog window to appear/ This
// dialog allows the user to select any number of files
// Flash Bug Warning: Flash limits the number of selectable files based on the combined length of the file names.
// If the selection name length is too long the dialog will fail in an unpredictable manner.  There is no work-around
// for this bug.
SWFUpload.prototype.selectFiles = function () {
    this.callFlash("SelectFiles");
};


// Public: startUpload starts uploading the first file in the queue unless
// the optional parameter 'fileID' specifies the ID
SWFUpload.prototype.startUpload = function (fileID) {
    this.callFlash("StartUpload", [fileID]);
};

// Public: cancelUpload cancels any queued file.  The fileID parameter may be the file ID or index.
// If you do not specify a fileID the current uploading file or first file in the queue is cancelled.
// If you do not want the uploadError event to trigger you can specify false for the triggerErrorEvent parameter.
SWFUpload.prototype.cancelUpload = function (fileID, triggerErrorEvent) {
    if (triggerErrorEvent !== false) {
        triggerErrorEvent = true;
    }
    this.callFlash("CancelUpload", [fileID, triggerErrorEvent]);
};

// Public: stopUpload stops the current upload and requeues the file at the beginning of the queue.
// If nothing is currently uploading then nothing happens.
SWFUpload.prototype.stopUpload = function () {
    this.callFlash("StopUpload");
};

/* ************************
 * Settings methods
 *   These methods change the SWFUpload settings.
 *   SWFUpload settings should not be changed directly on the settings object
 *   since many of the settings need to be passed to Flash in order to take
 *   effect.
 * *********************** */

// Public: getStats gets the file statistics object.
SWFUpload.prototype.getStats = function () {
    return this.callFlash("GetStats");
};

// Public: setStats changes the SWFUpload statistics.  You shouldn't need to
// change the statistics but you can.  Changing the statistics does not
// affect SWFUpload accept for the successful_uploads count which is used
// by the upload_limit setting to determine how many files the user may upload.
SWFUpload.prototype.setStats = function (statsObject) {
    this.callFlash("SetStats", [statsObject]);
};

// Public: getFile retrieves a File object by ID or Index.  If the file is
// not found then 'null' is returned.
SWFUpload.prototype.getFile = function (fileID) {
    if (typeof(fileID) === "number") {
        return this.callFlash("GetFileByIndex", [fileID]);
    } else {
        return this.callFlash("GetFile", [fileID]);
    }
};

// Public: addFileParam sets a name/value pair that will be posted with the
// file specified by the Files ID.  If the name already exists then the
// exiting value will be overwritten.
SWFUpload.prototype.addFileParam = function (fileID, name, value) {
    return this.callFlash("AddFileParam", [fileID, name, value]);
};

// Public: removeFileParam removes a previously set (by addFileParam) name/value
// pair from the specified file.
SWFUpload.prototype.removeFileParam = function (fileID, name) {
    this.callFlash("RemoveFileParam", [fileID, name]);
};
// Public: setFileTypes changes the file_types setting and the file_types_description setting
SWFUpload.prototype.setFileTypes = function (types, description) {
    this.settings.file_types = types;
    this.settings.file_types_description = description;
    this.callFlash("SetFileTypes", [types, description]);
};

// Public: setFileSizeLimit changes the file_size_limit setting
SWFUpload.prototype.setFileSizeLimit = function (fileSizeLimit) {
    this.settings.file_size_limit = fileSizeLimit;
    this.callFlash("SetFileSizeLimit", [fileSizeLimit]);
};

// Public: setFileUploadLimit changes the file_upload_limit setting
SWFUpload.prototype.setFileUploadLimit = function (fileUploadLimit) {
    this.settings.file_upload_limit = fileUploadLimit;
    this.callFlash("SetFileUploadLimit", [fileUploadLimit]);
};

// Public: setFileQueueLimit changes the file_queue_limit setting
SWFUpload.prototype.setFileQueueLimit = function (fileQueueLimit) {
    this.settings.file_queue_limit = fileQueueLimit;
    this.callFlash("SetFileQueueLimit", [fileQueueLimit]);
};

// Public: setFilePostName changes the file_post_name setting
SWFUpload.prototype.setFilePostName = function (filePostName) {
    this.settings.file_post_name = filePostName;
    this.callFlash("SetFilePostName", [filePostName]);
};

// Public: setUseQueryString changes the use_query_string setting
SWFUpload.prototype.setUseQueryString = function (useQueryString) {
    this.settings.use_query_string = useQueryString;
    this.callFlash("SetUseQueryString", [useQueryString]);
};

// Public: setRequeueOnError changes the requeue_on_error setting
SWFUpload.prototype.setRequeueOnError = function (requeueOnError) {
    this.settings.requeue_on_error = requeueOnError;
    this.callFlash("SetRequeueOnError", [requeueOnError]);
};

// Public: setHTTPSuccess changes the http_success setting
SWFUpload.prototype.setHTTPSuccess = function (http_status_codes) {
    if (typeof http_status_codes === "string") {
        http_status_codes = http_status_codes.replace(" ", "").split(",");
    }

    this.settings.http_success = http_status_codes;
    this.callFlash("SetHTTPSuccess", [http_status_codes]);
};


// Public: setDebugEnabled changes the debug_enabled setting
SWFUpload.prototype.setDebugEnabled = function (debugEnabled) {
    this.settings.debug_enabled = debugEnabled;
    this.callFlash("SetDebugEnabled", [debugEnabled]);
};

// Public: setButtonImageURL loads a button image sprite
SWFUpload.prototype.setButtonImageURL = function (buttonImageURL) {
    if (buttonImageURL == undefined) {
        buttonImageURL = "";
    }

    this.settings.button_image_url = buttonImageURL;
    this.callFlash("SetButtonImageURL", [buttonImageURL]);
};

// Public: setButtonDimensions resizes the Flash Movie and button
SWFUpload.prototype.setButtonDimensions = function (width, height) {
    this.settings.button_width = width;
    this.settings.button_height = height;

    var movie = this.getMovieElement();
    if (movie != undefined) {
        movie.style.width = width + "px";
        movie.style.height = height + "px";
    }

    this.callFlash("SetButtonDimensions", [width, height]);
};
// Public: setButtonText Changes the text overlaid on the button
SWFUpload.prototype.setButtonText = function (html) {
    this.settings.button_text = html;
    this.callFlash("SetButtonText", [html]);
};
// Public: setButtonTextPadding changes the top and left padding of the text overlay
SWFUpload.prototype.setButtonTextPadding = function (left, top) {
    this.settings.button_text_top_padding = top;
    this.settings.button_text_left_padding = left;
    this.callFlash("SetButtonTextPadding", [left, top]);
};

// Public: setButtonTextStyle changes the CSS used to style the HTML/Text overlaid on the button
SWFUpload.prototype.setButtonTextStyle = function (css) {
    this.settings.button_text_style = css;
    this.callFlash("SetButtonTextStyle", [css]);
};
// Public: setButtonDisabled disables/enables the button
SWFUpload.prototype.setButtonDisabled = function (isDisabled) {
    this.settings.button_disabled = isDisabled;
    this.callFlash("SetButtonDisabled", [isDisabled]);
};
// Public: setButtonAction sets the action that occurs when the button is clicked
SWFUpload.prototype.setButtonAction = function (buttonAction) {
    this.settings.button_action = buttonAction;
    this.callFlash("SetButtonAction", [buttonAction]);
};

// Public: setButtonCursor changes the mouse cursor displayed when hovering over the button
SWFUpload.prototype.setButtonCursor = function (cursor) {
    this.settings.button_cursor = cursor;
    this.callFlash("SetButtonCursor", [cursor]);
};

/* *******************************
 Flash Event Interfaces
 These functions are used by Flash to trigger the various
 events.

 All these functions a Private.

 Because the ExternalInterface library is buggy the event calls
 are added to a queue and the queue then executed by a setTimeout.
 This ensures that events are executed in a determinate order and that
 the ExternalInterface bugs are avoided.
 ******************************* */

SWFUpload.prototype.queueEvent = function (handlerName, argumentArray) {
    // Warning: Don't call this.debug inside here or you'll create an infinite loop

    if (argumentArray == undefined) {
        argumentArray = [];
    } else if (!(argumentArray instanceof Array)) {
        argumentArray = [argumentArray];
    }

    var self = this;
    if (typeof this.settings[handlerName] === "function") {
        // Queue the event
        this.eventQueue.push(function () {
            this.settings[handlerName].apply(this, argumentArray);
        });

        // Execute the next queued event
        setTimeout(function () {
            self.executeNextEvent();
        }, 0);

    } else if (this.settings[handlerName] !== null) {
        console.log("Event handler " + handlerName + " is unknown or is not a function");
    }
};

// Private: Causes the next event in the queue to be executed.  Since events are queued using a setTimeout
// we must queue them in order to garentee that they are executed in order.
SWFUpload.prototype.executeNextEvent = function () {
    // Warning: Don't call this.debug inside here or you'll create an infinite loop

    var f = this.eventQueue ? this.eventQueue.shift() : null;
    if (typeof(f) === "function") {
        f.apply(this);
    }
};

// Private: unescapeFileParams is part of a workaround for a flash bug where objects passed through ExternalInterface cannot have
// properties that contain characters that are not valid for JavaScript identifiers. To work around this
// the Flash Component escapes the parameter names and we must unescape again before passing them along.
SWFUpload.prototype.unescapeFilePostParams = function (file) {
    var reg = /[$]([0-9a-f]{4})/i;
    var unescapedPost = {};
    var uk;

    if (file != undefined) {
        for (var k in file.post) {
            if (file.post.hasOwnProperty(k)) {
                uk = k;
                var match;
                while ((match = reg.exec(uk)) !== null) {
                    uk = uk.replace(match[0], String.fromCharCode(parseInt("0x" + match[1], 16)));
                }
                unescapedPost[uk] = file.post[k];
            }
        }

        file.post = unescapedPost;
    }

    return file;
};

// Private: Called by Flash to see if JS can call in to Flash (test if External Interface is working)
SWFUpload.prototype.testExternalInterface = function () {
    try {
        return this.callFlash("TestExternalInterface");
    } catch (ex) {
        return false;
    }
};

// Private: This event is called by Flash when it has finished loading. Don't modify this.
// Use the swfupload_loaded_handler event setting to execute custom code when SWFUpload has loaded.
SWFUpload.prototype.flashReady = function () {
    // Check that the movie element is loaded correctly with its ExternalInterface methods defined
    var movieElement = this.getMovieElement();

    if (!movieElement) {
        this.debug("Flash called back ready but the flash movie can't be found.");
        return;
    }

    this.cleanUp(movieElement);

    this.queueEvent("swfupload_loaded_handler");
};

// Private: removes Flash added fuctions to the DOM node to prevent memory leaks in IE.
// This function is called by Flash each time the ExternalInterface functions are created.
SWFUpload.prototype.cleanUp = function (movieElement) {
    // Pro-actively unhook all the Flash functions
    try {
        if (this.movieElement && typeof(movieElement.CallFunction) === "unknown") { // We only want to do this in IE
            this.debug("Removing Flash functions hooks (this should only run in IE and should prevent memory leaks)");
            for (var key in movieElement) {
                try {
                    if (typeof(movieElement[key]) === "function") {
                        movieElement[key] = null;
                    }
                } catch (ex) {
                }
            }
        }
    } catch (ex1) {

    }

};


/* This is a chance to do something before the browse window opens */
SWFUpload.prototype.fileDialogStart = function () {
    this.queueEvent("file_dialog_start_handler");
};


/* Called when a file is successfully added to the queue. */
SWFUpload.prototype.fileQueued = function (file) {
    file = this.unescapeFilePostParams(file);
    this.queueEvent("file_queued_handler", file);
};


/* Handle errors that occur when an attempt to queue a file fails. */
SWFUpload.prototype.fileQueueError = function (file, errorCode, message) {
    file = this.unescapeFilePostParams(file);
    this.queueEvent("file_queue_error_handler", [file, errorCode, message]);
};

/* Called after the file dialog has closed and the selected files have been queued.
 You could call startUpload here if you want the queued files to begin uploading immediately. */
SWFUpload.prototype.fileDialogComplete = function (numFilesSelected, numFilesQueued) {
    this.queueEvent("file_dialog_complete_handler", [numFilesSelected, numFilesQueued]);
};

SWFUpload.prototype.uploadStart = function (file) {
    file = this.unescapeFilePostParams(file);
    this.queueEvent("return_upload_start_handler", file);
};

SWFUpload.prototype.returnUploadStart = function (file) {
    var returnValue;
    if (typeof this.settings.upload_start_handler === "function") {
        file = this.unescapeFilePostParams(file);
        returnValue = this.settings.upload_start_handler.call(this, file);
    } else if (this.settings.upload_start_handler != undefined) {
        console.log("upload_start_handler must be a function")
    }

    // Convert undefined to true so if nothing is returned from the upload_start_handler it is
    // interpretted as 'true'.
    if (returnValue === undefined) {
        returnValue = true;
    }

    returnValue = !!returnValue;

    this.callFlash("ReturnUploadStart", [returnValue]);
};


SWFUpload.prototype.uploadProgress = function (file, bytesComplete, bytesTotal) {
    file = this.unescapeFilePostParams(file);
    this.queueEvent("upload_progress_handler", [file, bytesComplete, bytesTotal]);
};

SWFUpload.prototype.uploadError = function (file, errorCode, message) {
    file = this.unescapeFilePostParams(file);
    this.queueEvent("upload_error_handler", [file, errorCode, message]);
};

SWFUpload.prototype.uploadSuccess = function (file, serverData) {
    file = this.unescapeFilePostParams(file);
    this.queueEvent("upload_success_handler", [file, serverData]);
};

SWFUpload.prototype.uploadComplete = function (file) {
    file = this.unescapeFilePostParams(file);
    this.queueEvent("upload_complete_handler", file);
};

/* Called by SWFUpload JavaScript and Flash functions when debug is enabled. By default it writes messages to the
 internal debug console.  You can override this event and have messages written where you want. */
SWFUpload.prototype.debug = function (message) {
    this.queueEvent("debug_handler", message);
};


/* **********************************
 Debug Console
 The debug console is a self contained, in page location
 for debug message to be sent.  The Debug Console adds
 itself to the body if necessary.

 The console is automatically scrolled as messages appear.

 If you are using your own debug handler or when you deploy to production and
 have debug disabled you can remove these functions to reduce the file size
 and complexity.
 ********************************** */

// Private: debugMessage is the default debug_handler.  If you want to print debug messages
// call the debug() function.  When overriding the function your own function should
// check to see if the debug setting is true before outputting debug information.
SWFUpload.prototype.debugMessage = function (message) {
    if (this.settings.debug) {
        var exceptionMessage, exceptionValues = [];

        // Check for an exception object and print it nicely
        if (typeof message === "object" && typeof message.name === "string" && typeof message.message === "string") {
            for (var key in message) {
                if (message.hasOwnProperty(key)) {
                    exceptionValues.push(key + ": " + message[key]);
                }
            }
            exceptionMessage = exceptionValues.join("\n") || "";
            exceptionValues = exceptionMessage.split("\n");
            exceptionMessage = "EXCEPTION: " + exceptionValues.join("\nEXCEPTION: ");
            SWFUpload.Console.writeLine(exceptionMessage);
        } else {
            SWFUpload.Console.writeLine(message);
        }
    }
};

SWFUpload.Console = {};
SWFUpload.Console.writeLine = function (message) {
    var console, documentForm;

    try {
        console = document.getElementById("SWFUpload_Console");

        if (!console) {
            documentForm = document.createElement("form");
            document.getElementsByTagName("body")[0].appendChild(documentForm);

            console = document.createElement("textarea");
            console.id = "SWFUpload_Console";
            console.style.fontFamily = "monospace";
            console.setAttribute("wrap", "off");
            console.wrap = "off";
            console.style.overflow = "auto";
            console.style.width = "700px";
            console.style.height = "350px";
            console.style.margin = "5px";
            documentForm.appendChild(console);
        }

        console.value += message + "\n";

        console.scrollTop = console.scrollHeight - console.clientHeight;
    } catch (ex) {
        alert("Exception: " + ex.name + " Message: " + ex.message);
    }
};


Date.prototype.getElapsed = function (A) {
    return Math.abs((A || new Date()).getTime() - this.getTime())
};

UploadPanel = Ext.extend(Ext.Panel, {
    fileList: null,
    swfupload: null,
    progressBar: null,
    progressInfo: null,
    uploadInfoPanel: null,
    constructor: function (config) {
        this.progressInfo = {
            filesTotal: 0,
            filesUploaded: 0,
            bytesTotal: 0,
            bytesUploaded: 0,
            currentCompleteBytes: 0,
            lastBytes: 0,
            lastElapsed: 1,
            lastUpdate: null,
            timeElapsed: 1
        };
        this.uploadInfoPanel = new Ext.Panel({
            region: 'north',
            height: 65,
            baseCls: '',
            collapseMode: 'mini',
            split: true,
            border: false
        });
        this.progressBar = new Ext.ProgressBar({
            text: '等待中 0 %',
            animate: true
        });
        this.uploadInfoPanel.on('render', function () {
            this.getProgressTemplate().overwrite(this.uploadInfoPanel.body, {
                filesUploaded: 0,
                filesTotal: 0,
                bytesUploaded: '0 bytes',
                bytesTotal: '0 bytes',
                timeElapsed: '00:00:00',
                timeLeft: '00:00:00',
                speedLast: '0 bytes/s',
                speedAverage: '0 bytes/s'
            });
        }, this);
        this.fileList = new Ext.grid.GridPanel({
            border: false,
            enableColumnMove: false,
            enableHdMenu: false,
            viewConfig: {
                forceFit: true
            },
            columns: [new Ext.grid.RowNumberer(), {
                header: '文件名',
                width: 100,
                dataIndex: 'fileName',
                sortable: false,
                fixed: true,
                renderer: this.formatFileName
            }, {
                header: '大小',
                width: 80,
                dataIndex: 'fileSize',
                sortable: false,
                fixed: true,
                renderer: this.formatFileSize,
                align: 'right'
            }, {
                header: '类型',
                width: 60,
                dataIndex: 'fileType',
                sortable: false,
                fixed: true,
                renderer: this.formatIcon,
                align: 'center'
            }, {
                header: '进度',
                width: 100,
                dataIndex: '',
                sortable: false,
                fixed: true,
                renderer: this.formatProgressBar,
                align: 'center'
            }, {
                header: '&nbsp;',
                dataIndex: 'fileState',
                renderer: this.formatState,
                sortable: false,
                fixed: true,
                align: 'center'
            }],
            ds: new Ext.data.SimpleStore({
                fields: ['fileId', 'fileName', 'fileSize', 'fileType', 'fileState']
            }),
            bbar: [this.progressBar],
            tbar: [
                {
                    text: '添加文件',
                    iconCls: 'db-icn-add'
                },
                {
                    text: '开始上传',
                    iconCls: 'db-icn-upload_',
                    handler: this.startUpload,
                    scope: this
                },
                {
                    text: '停止上传',
                    iconCls: 'db-icn-stop',
                    handler: this.stopUpload,
                    scope: this
                },
                {
                    text: '取消队列',
                    iconCls: 'db-icn-cross',
                    handler: this.cancelQueue,
                    scope: this
                },
                {
                    text: '清空列表',
                    iconCls: 'db-icn-trash',
                    handler: this.clearList,
                    scope: this
                }
            ],
            listeners: {
                cellclick: {
                    fn: function (grid, rowIndex, columnIndex, e) {
                        if (columnIndex == 5) {
                            var record = grid.getSelectionModel().getSelected();
                            var fileId = record.data.fileId;
                            var file = this.swfupload.getFile(fileId);
                            if (file) {
                                if (file.filestatus != SWFUpload.FILE_STATUS.CANCELLED) {
                                    this.swfupload.cancelUpload(fileId);
                                    if (record.data.fileState != SWFUpload.FILE_STATUS.CANCELLED) {
                                        record.set('fileState', SWFUpload.FILE_STATUS.CANCELLED);
                                        record.commit();
                                        this.onCancelQueue(fileId);
                                    }
                                }
                            }
                        }
                    },
                    scope: this
                },
                render: {
                    fn: function () {
                        var grid = this.get(1).get(0);
                        var em = grid.getTopToolbar().get(0).el.child('em');
                        var placeHolderId = Ext.id();
                        em.setStyle({
                            position: 'relative',
                            display: 'block'
                        });
                        em.createChild({
                            tag: 'div',
                            id: placeHolderId
                        });
                        var settings = {
                            upload_url: this.uploadUrl,
                            //post_params : Ext.isEmpty(this.postParams) ? {} : this.postParams,
                            flash_url: Ext.isEmpty(this.flashUrl)
                                ? 'http://www.swfupload.org/swfupload.swf'
                                : this.flashUrl,
                            file_post_name: Ext.isEmpty(this.filePostName) ? 'myUpload' : this.filePostName,
                            file_size_limit: Ext.isEmpty(this.fileSize) ? '100 MB' : this.fileSize,
                            file_types: Ext.isEmpty(this.fileTypes) ? '*.*' : this.fileTypes,
                            file_types_description: this.fileTypesDescription,
                            source_window_id:this.sourceWindowId,
                            use_query_string: true,
                            debug: false,
                            button_width: '73',
                            button_height: '20',
                            button_placeholder_id: placeHolderId,
                            button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
                            button_cursor: SWFUpload.CURSOR.HAND,
                            custom_settings: {
                                scope_handler: this
                            },
                            file_queued_handler: this.onFileQueued,
                            file_queue_error_handler: this.onFileQueueError,
                            file_dialog_complete_handler: this.onFileDialogComplete,
                            upload_start_handler: this.onUploadStart,
                            upload_progress_handler: this.onUploadProgress,
                            upload_error_handler: this.onUploadError,
                            upload_success_handler: this.onUploadSuccess,
                            upload_complete_handler: this.onUploadComplete
                        };
                        this.swfupload = new SWFUpload(settings);
                        this.swfupload.uploadStopped = false;
                        Ext.get(this.swfupload.movieName).setStyle({
                            position: 'absolute',
                            top: 0,
                            left: 0
                        });
                        this.resizeProgressBar();
                        this.on('resize', this.resizeProgressBar, this);
                    },
                    scope: this,
                    delay: 100
                }
            }

        });
        UploadPanel.superclass.constructor.call(this, Ext.applyIf(config || {}, {
            layout: 'border',
            width: 500,
            height: 500,
            minWidth: 450,
            minHeight: 500,
            split: true,
            items: [this.uploadInfoPanel, {
                region: 'center',
                layout: 'fit',
                margins: '0 -1 -1 -1',
                items: [this.fileList]
            }]
        }));
    },
    resizeProgressBar: function () {
        this.progressBar.setWidth(this.el.getWidth() - 5);
    },
    startUpload: function () {
        if (this.swfupload) {
            this.swfupload.uploadStopped = false;
            /*this.swfupload.setPostParams(post_params);*/
            this.swfupload.startUpload();
        }
    },
    stopUpload: function () {
        if (this.swfupload) {
            this.swfupload.uploadStopped = true;
            this.swfupload.stopUpload();
        }
    },
    cancelQueue: function () {
        if (this.swfupload) {
            this.swfupload.stopUpload();
            var stats = this.swfupload.getStats();
            while (stats.files_queued > 0) {
                this.swfupload.cancelUpload();
                stats = this.swfupload.getStats();
            }
            this.fileList.getStore().each(function (record) {
                switch (record.data.fileState) {
                    case SWFUpload.FILE_STATUS.QUEUED :
                    case SWFUpload.FILE_STATUS.IN_PROGRESS :
                        record.set('fileState', SWFUpload.FILE_STATUS.CANCELLED);
                        record.commit();
                        this.onCancelQueue(record.data.fileId);
                        break;
                    default :
                        break;
                }
            }, this);
        }
    },
    clearList: function () {
        var store = this.fileList.getStore();
        store.each(function (record) {
            if (record.data.fileState != SWFUpload.FILE_STATUS.QUEUED
                && record.data.fileState != SWFUpload.FILE_STATUS.IN_PROGRESS) {
                store.remove(record);
            }
        });
    },
    getProgressTemplate: function () {
        var tpl = new Ext.Template('<table class="upload-progress-table"><tbody>',
            '<tr><td class="upload-progress-label"><nobr>已上传数:</nobr></td>',
            '<td class="upload-progress-value"><nobr>{filesUploaded} / {filesTotal}</nobr></td>',
            '<td class="upload-progress-label"><nobr>上传状态:</nobr></td>',
            '<td class="upload-progress-value"><nobr>{bytesUploaded} / {bytesTotal}</nobr></td></tr>',
            '<tr><td class="upload-progress-label"><nobr>已用时间:</nobr></td>',
            '<td class="upload-progress-value"><nobr>{timeElapsed}</nobr></td>',
            '<td class="upload-progress-label"><nobr>剩余时间:</nobr></td>',
            '<td class="upload-progress-value"><nobr>{timeLeft}</nobr></td></tr>',
            '<tr><td class="upload-progress-label"><nobr>当前速度:</nobr></td>',
            '<td class="upload-progress-value"><nobr>{speedLast}</nobr></td>',
            '<td class="upload-progress-label"><nobr>平均速度:</nobr></td>',
            '<td class="upload-progress-value"><nobr>{speedAverage}</nobr></td></tr>', '</tbody></table>');
        tpl.compile();
        return tpl;
    },
    updateProgressInfo: function () {
        this.getProgressTemplate().overwrite(this.uploadInfoPanel.body, this.formatProgress(this.progressInfo));
    },
    formatProgress: function (info) {
        var r = {};
        r.filesUploaded = String.leftPad(info.filesUploaded, 3, '&nbsp;');
        r.filesTotal = info.filesTotal;
        r.bytesUploaded = String.leftPad(Ext.util.Format.fileSize(info.bytesUploaded), 6, '&#160;');
        r.bytesTotal = Ext.util.Format.fileSize(info.bytesTotal);
        r.timeElapsed = this.formatTime(info.timeElapsed);
        r.speedAverage = Ext.util.Format.fileSize(Math.ceil(1000 * info.bytesUploaded / info.timeElapsed)) + '/s';
        r.timeLeft = this.formatTime((info.bytesUploaded === 0) ? 0 : info.timeElapsed
            * (info.bytesTotal - info.bytesUploaded) / info.bytesUploaded);
        var caleSpeed = 1000 * info.lastBytes / info.lastElapsed;
        r.speedLast = Ext.util.Format.fileSize(caleSpeed < 0 ? 0 : caleSpeed) + '/s';
        var p = info.bytesUploaded / info.bytesTotal;
        p = p || 0;
        this.progressBar.updateProgress(p, "上传中 " + Math.ceil(p * 100) + " %");
        return r;
    },
    formatTime: function (milliseconds) {
        var seconds = parseInt(milliseconds / 1000, 10);
        var s = 0;
        var m = 0;
        var h = 0;
        if (3599 < seconds) {
            h = parseInt(seconds / 3600, 10);
            seconds -= h * 3600;
        }
        if (59 < seconds) {
            m = parseInt(seconds / 60, 10);
            seconds -= m * 60;
        }
        m = String.leftPad(m, 2, '0');
        h = String.leftPad(h, 2, '0');
        s = String.leftPad(seconds, 2, '0');
        return h + ':' + m + ':' + s;
    },
    formatFileSize: function (_v, celmeta, record) {
        var size = Ext.util.Format.fileSize(_v);
        return '<div id="fileSize_' + record.data.fileId + '">' + size + '</div>';
    },
    formatFileName: function (_v, cellmeta, record) {
        return '<div id="fileName_' + record.data.fileId + '">' + _v + '</div>';
    },
    formatIcon: function (_v, cellmeta, record) {
        var returnValue = '';
        var extensionName = _v.substring(1);
        var fileId = record.data.fileId;
        if (_v) {
            var css = '.db-ft-' + extensionName.toLowerCase() + '-small';
            if (Ext.isEmpty(Ext.util.CSS.getRule(css), true)) { // 判断样式是否存在
                returnValue = '<div id="fileType_' + fileId
                    + '" class="db-ft-unknown-small" style="height: 16px;background-repeat: no-repeat;">'
                    + '&nbsp;&nbsp;&nbsp;&nbsp;' + extensionName.toUpperCase() + '</div>';
            } else {
                returnValue = '<div id="fileType_' + fileId + '" class="db-ft-' + extensionName.toLowerCase()
                    + '-small" style="height: 16px;background-repeat: no-repeat;"/>&nbsp;&nbsp;&nbsp;&nbsp;'
                    + extensionName.toUpperCase();
                +'</div>';
            }
            return returnValue;
        }
        return '<div id="fileType_'
            + fileId
            + '" class="db-ft-unknown-small" style="height: 16px;background-repeat: no-repeat;"/>&nbsp;&nbsp;&nbsp;&nbsp;'
            + extensionName.toUpperCase() + '</div>';
    },
    formatProgressBar: function (_v, cellmeta, record) {
        var returnValue = '';
        switch (record.data.fileState) {
            case SWFUpload.FILE_STATUS.COMPLETE :
                if (Ext.isIE) {
                    returnValue = '<div class="x-progress-wrap" style="height: 18px">'
                        + '<div class="x-progress-inner">'
                        + '<div style="width: 100%;" class="x-progress-bar x-progress-text">' + '100 %'
                    '</div>' + '</div>' + '</div>';
                } else {
                    returnValue = '<div class="x-progress-wrap" style="height: 18px">'
                        + '<div class="x-progress-inner">' + '<div id="progressBar_' + record.data.fileId
                        + '" style="width: 100%;" class="x-progress-bar">' + '</div>' + '<div id="progressText_'
                        + record.data.fileId
                        + '" style="width: 100%;" class="x-progress-text x-progress-text-back" />100 %</div>'
                    '</div>' + '</div>';
                }
                break;
            default :
                returnValue = '<div class="x-progress-wrap" style="height: 18px">' + '<div class="x-progress-inner">'
                    + '<div id="progressBar_' + record.data.fileId + '" style="width: 0%;" class="x-progress-bar">'
                    + '</div>' + '<div id="progressText_' + record.data.fileId
                    + '" style="width: 100%;" class="x-progress-text x-progress-text-back" />0 %</div>'
                '</div>' + '</div>';
                break;
        }
        return returnValue;
    },
    formatState: function (_v, cellmeta, record) {
        var returnValue = '';
        switch (_v) {
            case SWFUpload.FILE_STATUS.QUEUED :
                returnValue = '<span id="' + record.id + '"><div id="fileId_' + record.data.fileId
                    + '" class="ux-cell-icon-delete"/></span>';
                break;
            case SWFUpload.FILE_STATUS.CANCELLED :
                returnValue = '<span id="' + record.id + '"><div id="fileId_' + record.data.fileId
                    + '" class="ux-cell-icon-clear"/></span>';
                break;
            case SWFUpload.FILE_STATUS.COMPLETE :
                returnValue = '<span id="' + record.id + '"><div id="fileId_' + record.data.fileId
                    + '" class="ux-cell-icon-completed"/></span>';
                break;
            default :
                alert('没有设置图表状态');
                break;
        }
        return returnValue;
    },
    onClose: function () {
        this.close();
    },
    onCancelQueue: function (fileId) {
        Ext.getDom('fileName_' + fileId).className = 'ux-cell-color-gray';// 设置文字颜色为灰色
        Ext.getDom('fileSize_' + fileId).className = 'ux-cell-color-gray';
        Ext.DomHelper.applyStyles('fileType_' + fileId, 'font-style:italic;text-decoration: line-through;color:gray');
    },
    onFileQueued: function (file) {
        var thiz = this.customSettings.scope_handler;
        if (file.size > 1024000) {
            Ext.Msg.confirm("确认", "该图片大小已经超过了1M，请确认是否上传", function (btn) {
                if (btn == 'yes') {
                    thiz.fileList.getStore().add(new UploadPanel.FileRecord({
                        fileId: file.id,
                        fileName: file.name,
                        fileSize: file.size,
                        fileType: file.type,
                        fileState: file.filestatus
                    }));
                    thiz.progressInfo.filesTotal += 1;
                    thiz.progressInfo.bytesTotal += file.size;
                    thiz.updateProgressInfo();
                } else {
                    thiz.swfupload.stopUpload();
                    var stats = thiz.swfupload.getStats();
                    thiz.swfupload.cancelUpload(file.id, null);

                }
            });
        } else {
            thiz.fileList.getStore().add(new UploadPanel.FileRecord({
                fileId: file.id,
                fileName: file.name,
                fileSize: file.size,
                fileType: file.type,
                fileState: file.filestatus
            }));
            thiz.progressInfo.filesTotal += 1;
            thiz.progressInfo.bytesTotal += file.size;
            thiz.updateProgressInfo();
        }


    },
    onQueueError: function (file, errorCode, message) {
        var thiz = this.customSettings.scope_handler;
        try {
            if (errorCode != SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED) {
                thiz.progressInfo.filesTotal -= 1;
                thiz.progressInfo.bytesTotal -= file.size;
            }
            thiz.progressInfo.bytesUploaded -= fpg.getBytesCompleted();
            thiz.updateProgressInfo();
        } catch (ex) {
            this.debug(ex);
        }
    },
    onFileDialogComplete: function (selectedFilesCount, queuedFilesCount) {
        //alert("成功");
    },
    onUploadProgress: function (file, completeBytes, bytesTotal) {
        var percent = Math.ceil((completeBytes / bytesTotal) * 100);
        Ext.getDom('progressBar_' + file.id).style.width = percent + "%";
        Ext.getDom('progressText_' + file.id).innerHTML = percent + " %";

        var thiz = this.customSettings.scope_handler;
        var bytes_added = completeBytes - thiz.progressInfo.currentCompleteBytes;
        thiz.progressInfo.bytesUploaded += Math.abs(bytes_added < 0 ? 0 : bytes_added);
        thiz.progressInfo.currentCompleteBytes = completeBytes;
        if (thiz.progressInfo.lastUpdate) {
            thiz.progressInfo.lastElapsed = thiz.progressInfo.lastUpdate.getElapsed();
            thiz.progressInfo.timeElapsed += thiz.progressInfo.lastElapsed;
        }
        thiz.progressInfo.lastBytes = bytes_added;
        thiz.progressInfo.lastUpdate = new Date();
        thiz.updateProgressInfo();
    },
    onUploadError: function (file, errorCode, message) {
        var thiz = this.customSettings.scope_handler;
        switch (errorCode) {
            case SWFUpload.UPLOAD_ERROR.FILE_CANCELLED :
                thiz.progressInfo.filesTotal -= 1;
                thiz.progressInfo.bytesTotal -= file.size;
                thiz.updateProgressInfo();
                break;
            case SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED :
        }
    },
    onUploadSuccess: function (file, serverData) {
        var thiz = this.customSettings.scope_handler;
        if (Ext.util.JSON.decode(serverData).success) {
            var record = thiz.fileList.getStore().getById(Ext.getDom('fileId_' + file.id).parentNode.id);
            record.set('fileState', file.filestatus);
            record.commit();
            Ext.getCmp(this.sourceWindowId).getStore().reload();
            //  Ext.getCmp('spaceImage_win').close();
        } else {
            var msg = Ext.util.JSON.decode(serverData).msg;
            Ext.Msg.alert('信息', msg);
        }
        thiz.progressInfo.filesUploaded += 1;
        thiz.updateProgressInfo();
    },
    onUploadComplete: function (file) {
        if (this.getStats().files_queued > 0 && this.uploadStopped == false) {
            this.startUpload();
        }
    }
});

UploadPanel.FileRecord = Ext.data.Record.create([
    {
        name: 'fileId'
    },
    {
        name: 'fileName'
    },
    {
        name: 'fileSize'
    },
    {
        name: 'fileType'
    },
    {
        name: 'fileState'
    }
]);

Ext.reg('uploadpanel', UploadPanel);









