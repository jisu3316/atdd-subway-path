package nextstep.subway.line.service;

import nextstep.subway.line.dto.LineDto;
import nextstep.subway.line.dto.ModifyLineRequest;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.section.entity.Section;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineDto createLine(LineDto dto) {
        Station upStation = stationRepository.findById(dto.getUpStationId())
                .orElseThrow(() -> new IllegalArgumentException("상행역이 존재하지 않습니다."));
        Station downStation = stationRepository.findById(dto.getDownStationId())
                .orElseThrow(() -> new IllegalArgumentException("하행역 존재하지 않습니다."));
        Section section = Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(dto.getDistance())
                .build();
        Line savedLine = lineRepository.save(dto.toEntity(upStation, downStation, section));

        return LineDto.of(savedLine);
    }

    @Transactional(readOnly = true)
    public List<LineDto> getLines() {
        List<Line> lines = lineRepository.findAll();
        return LineDto.toLineDtos(lines);
    }

    @Transactional(readOnly = true)
    public LineDto getLine(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("노선이 존재하지 않습니다."));
        return LineDto.of(line);
    }

    public void modifyLine(Long id, ModifyLineRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("노선이 존재하지 않습니다."));
        line.modifySubwayLine(request.getName(), request.getColor());
    }


    public void deleteLine(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("노선이 존재하지 않습니다."));
        lineRepository.delete(line);
    }
}
