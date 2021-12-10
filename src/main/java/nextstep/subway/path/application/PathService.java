package nextstep.subway.path.application;

import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.infra.PathFinder;
import nextstep.subway.path.domain.Path;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathService {

    private final LineRepository lineRepository;
    private final StationService stationService;
    private final PathFinder pathFinder;

    public PathService(LineRepository lineRepository, StationService stationService, PathFinder pathFinder) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.pathFinder = pathFinder;
    }

    public PathResponse findShortestPath(long source, long target) {

        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);

        List<Line> lines = lineRepository.findAll();

        Path path = new Path(pathFinder);
        List<Station> paths = path.findPaths(lines, sourceStation, targetStation);
        Distance distance = path.findPathWeight(lines, sourceStation, targetStation);
        return new PathResponse(paths, distance);
    }
}
