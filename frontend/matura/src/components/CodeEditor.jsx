import {Editor} from "@monaco-editor/react";
import PropTypes from "prop-types";

export const CodeEditor = ({ language, startingCode }) => {

    return (
        <Editor
            defaultLanguage={language}
            defaultValue={startingCode}
            theme='vs-dark'
            height="70vh"
            width='100%'
            options={{
                wordWrap: 'on',
                minimap: {
                    enabled: false
                },
                inlayHints: {
                    enabled: 'on',
                },
                scrollbar: {
                    verticalScrollbarSize: 0,
                    horizontalScrollbarSize: 0
                }
            }}
        />
    )
}
CodeEditor.propTypes = {
    language: PropTypes.string.isRequired,
    startingCode: PropTypes.string
}