package io.studi.backend.dtos.whiteboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhiteboardCanvasState {
    private String version;
    private List<Object> objects;
}
